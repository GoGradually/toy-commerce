package me.gogradually.toycommerce.interfaces.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.gogradually.toycommerce.domain.product.Product;
import me.gogradually.toycommerce.domain.product.ProductRepository;
import me.gogradually.toycommerce.infrastructure.repository.product.JpaProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(CheckoutForceSoldOutConcurrencyE2ETest.ConcurrencyTestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CheckoutForceSoldOutConcurrencyE2ETest {

    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProductLockRaceCoordinator productLockRaceCoordinator;

    @AfterEach
    void tearDown() {
        productLockRaceCoordinator.clear();
    }

    @Test
    void shouldCheckoutFirstThenForceSoldOutCancelsCreatedOrder() throws Exception {
        Long memberId = 9101L;
        Long productId = createProduct("동시성 검증 상품", 15900, 5, "ACTIVE");
        addCartItem(memberId, productId, 2);
        productLockRaceCoordinator.arm(productId);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        try {
            Future<MvcResult> checkoutFuture = executorService.submit(() -> mockMvc.perform(post("/api/orders/checkout")
                            .header("X-Member-Id", String.valueOf(memberId)))
                    .andReturn());

            productLockRaceCoordinator.awaitCheckoutLockAcquired();

            Future<MvcResult> forceSoldOutFuture = executorService.submit(() -> mockMvc.perform(
                            patch("/api/admin/products/{productId}/force-sold-out", productId))
                    .andReturn());

            productLockRaceCoordinator.awaitForceSoldOutStarted();
            productLockRaceCoordinator.releaseCheckoutCommit();

            MvcResult checkoutResult = checkoutFuture.get(WAIT_TIMEOUT.toSeconds(), TimeUnit.SECONDS);
            MvcResult forceSoldOutResult = forceSoldOutFuture.get(WAIT_TIMEOUT.toSeconds(), TimeUnit.SECONDS);

            assertThat(checkoutResult.getResponse().getStatus()).isEqualTo(201);
            Long orderId = readLong(checkoutResult, "orderId");

            JsonNode forceSoldOutResponse = objectMapper.readTree(forceSoldOutResult.getResponse().getContentAsString());
            assertThat(forceSoldOutResult.getResponse().getStatus()).isEqualTo(200);
            assertThat(forceSoldOutResponse.path("success").asBoolean()).isTrue();
            assertThat(forceSoldOutResponse.path("data").path("status").asText()).isEqualTo("INACTIVE");
            assertThat(forceSoldOutResponse.path("data").path("stock").asInt()).isZero();

            mockMvc.perform(get("/api/orders/{orderId}", orderId)
                            .header("X-Member-Id", String.valueOf(memberId)))
                    .andExpect(status().isOk())
                    .andExpect(result -> {
                        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
                        assertThat(root.path("data").path("status").asText()).isEqualTo("CANCELLED");
                    });

            mockMvc.perform(get("/api/products/{productId}", productId))
                    .andExpect(status().isNotFound());
        } finally {
            executorService.shutdownNow();
        }
    }

    private void addCartItem(Long memberId, Long productId, int quantity) throws Exception {
        mockMvc.perform(post("/api/cart/items")
                        .header("X-Member-Id", String.valueOf(memberId))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddCartItemPayload(productId, quantity))))
                .andExpect(status().isOk());
    }

    private Long createProduct(String name, int price, int stock, String status) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/admin/products")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateProductPayload(
                                name,
                                new BigDecimal(price),
                                stock,
                                status
                        ))))
                .andExpect(status().isCreated())
                .andReturn();

        return readLong(result, "id");
    }

    private Long readLong(MvcResult result, String fieldName) throws Exception {
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        long value = root.path("data").path(fieldName).asLong();
        assertThat(value).isPositive();
        return value;
    }

    enum RequestType {
        CHECKOUT,
        FORCE_SOLD_OUT
    }

    private record AddCartItemPayload(Long productId, Integer quantity) {
    }

    private record CreateProductPayload(String name, BigDecimal price, Integer stock, String status) {
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class ConcurrencyTestConfig {

        @Bean
        ProductLockRaceCoordinator productLockRaceCoordinator() {
            return new ProductLockRaceCoordinator();
        }

        @Bean
        LockScopedRequestContext lockScopedRequestContext() {
            return new LockScopedRequestContext();
        }

        @Bean
        LockAwareRequestFilter lockAwareRequestFilter(LockScopedRequestContext lockScopedRequestContext) {
            return new LockAwareRequestFilter(lockScopedRequestContext);
        }

        @Bean
        FilterRegistrationBean<LockAwareRequestFilter> lockAwareRequestFilterRegistration(
                LockAwareRequestFilter lockAwareRequestFilter
        ) {
            FilterRegistrationBean<LockAwareRequestFilter> registration = new FilterRegistrationBean<>(lockAwareRequestFilter);
            registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
            return registration;
        }

        @Bean
        @Primary
        ProductRepository coordinatingProductRepository(
                JpaProductRepository delegate,
                ProductLockRaceCoordinator productLockRaceCoordinator,
                LockScopedRequestContext lockScopedRequestContext
        ) {
            return (ProductRepository) Proxy.newProxyInstance(
                    ProductRepository.class.getClassLoader(),
                    new Class<?>[]{ProductRepository.class},
                    (proxy, method, args) -> {
                        if ("findByIdForUpdate".equals(method.getName()) && args != null && args.length == 1) {
                            Long productId = (Long) args[0];
                            return coordinateFindByIdForUpdate(
                                    delegate,
                                    productId,
                                    productLockRaceCoordinator,
                                    lockScopedRequestContext
                            );
                        }

                        try {
                            return method.invoke(delegate, args);
                        } catch (InvocationTargetException exception) {
                            throw exception.getCause();
                        }
                    }
            );
        }

        private Optional<Product> coordinateFindByIdForUpdate(
                JpaProductRepository delegate,
                Long productId,
                ProductLockRaceCoordinator productLockRaceCoordinator,
                LockScopedRequestContext lockScopedRequestContext
        ) {
            if (!lockScopedRequestContext.shouldIntercept() || !productLockRaceCoordinator.isTarget(productId)) {
                return delegate.findByIdForUpdate(productId);
            }

            RequestType requestType = lockScopedRequestContext.currentType();
            lockScopedRequestContext.markIntercepted();

            if (requestType == RequestType.CHECKOUT) {
                Optional<Product> product = delegate.findByIdForUpdate(productId);
                productLockRaceCoordinator.signalCheckoutLockAcquired(productId);
                productLockRaceCoordinator.awaitCheckoutCommitRelease(productId);
                return product;
            }

            if (requestType == RequestType.FORCE_SOLD_OUT) {
                productLockRaceCoordinator.signalForceSoldOutStarted(productId);
                return delegate.findByIdForUpdate(productId);
            }

            return delegate.findByIdForUpdate(productId);
        }
    }

    static final class LockScopedRequestContext {

        private final ThreadLocal<RequestType> requestType = new ThreadLocal<>();
        private final ThreadLocal<Boolean> intercepted = new ThreadLocal<>();

        void open(RequestType type) {
            requestType.set(type);
            intercepted.set(Boolean.FALSE);
        }

        RequestType currentType() {
            return requestType.get();
        }

        boolean shouldIntercept() {
            return requestType.get() != null && !Boolean.TRUE.equals(intercepted.get());
        }

        void markIntercepted() {
            intercepted.set(Boolean.TRUE);
        }

        void clear() {
            requestType.remove();
            intercepted.remove();
        }
    }

    static final class LockAwareRequestFilter extends OncePerRequestFilter {

        private static final Pattern FORCE_SOLD_OUT_PATH = Pattern.compile("^/api/admin/products/\\d+/force-sold-out$");

        private final LockScopedRequestContext lockScopedRequestContext;

        private LockAwareRequestFilter(LockScopedRequestContext lockScopedRequestContext) {
            this.lockScopedRequestContext = lockScopedRequestContext;
        }

        @Override
        protected void doFilterInternal(
                HttpServletRequest request,
                HttpServletResponse response,
                FilterChain filterChain
        ) throws ServletException, IOException {
            RequestType requestType = resolveRequestType(request);
            if (requestType == null) {
                filterChain.doFilter(request, response);
                return;
            }

            lockScopedRequestContext.open(requestType);
            try {
                filterChain.doFilter(request, response);
            } finally {
                lockScopedRequestContext.clear();
            }
        }

        private RequestType resolveRequestType(HttpServletRequest request) {
            if ("POST".equals(request.getMethod()) && "/api/orders/checkout".equals(request.getRequestURI())) {
                return RequestType.CHECKOUT;
            }

            if ("PATCH".equals(request.getMethod()) && FORCE_SOLD_OUT_PATH.matcher(request.getRequestURI()).matches()) {
                return RequestType.FORCE_SOLD_OUT;
            }

            return null;
        }
    }

    static final class ProductLockRaceCoordinator {

        private final AtomicReference<ScenarioState> scenarioState = new AtomicReference<>();

        void arm(Long productId) {
            scenarioState.set(new ScenarioState(productId));
        }

        boolean isTarget(Long productId) {
            ScenarioState state = scenarioState.get();
            return state != null && state.productId().equals(productId);
        }

        void awaitCheckoutLockAcquired() {
            awaitLatch(requiredState().checkoutLockAcquired(), "Timed out waiting for checkout to acquire the product lock.");
        }

        void signalCheckoutLockAcquired(Long productId) {
            stateFor(productId).checkoutLockAcquired().countDown();
        }

        void awaitForceSoldOutStarted() {
            awaitLatch(requiredState().forceSoldOutStarted(), "Timed out waiting for force sold out to start.");
        }

        void signalForceSoldOutStarted(Long productId) {
            stateFor(productId).forceSoldOutStarted().countDown();
        }

        void awaitCheckoutCommitRelease(Long productId) {
            awaitLatch(stateFor(productId).allowCheckoutCommit(), "Timed out waiting to release checkout commit.");
        }

        void releaseCheckoutCommit() {
            requiredState().allowCheckoutCommit().countDown();
        }

        void clear() {
            scenarioState.set(null);
        }

        private ScenarioState requiredState() {
            ScenarioState state = scenarioState.get();
            if (state == null) {
                throw new IllegalStateException("The product lock race coordinator was used before it was armed.");
            }
            return state;
        }

        private ScenarioState stateFor(Long productId) {
            ScenarioState state = requiredState();
            if (!state.productId().equals(productId)) {
                throw new IllegalStateException("Unexpected product id for the armed concurrency scenario: " + productId);
            }
            return state;
        }

        private void awaitLatch(CountDownLatch latch, String message) {
            try {
                if (!latch.await(WAIT_TIMEOUT.toSeconds(), TimeUnit.SECONDS)) {
                    throw new IllegalStateException(message);
                }
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(message, exception);
            }
        }

        private record ScenarioState(
                Long productId,
                CountDownLatch checkoutLockAcquired,
                CountDownLatch forceSoldOutStarted,
                CountDownLatch allowCheckoutCommit
        ) {
            private ScenarioState(Long productId) {
                this(productId, new CountDownLatch(1), new CountDownLatch(1), new CountDownLatch(1));
            }
        }
    }
}
