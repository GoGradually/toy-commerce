package me.gogradually.toycommerce.interfaces.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import me.gogradually.toycommerce.application.order.OrderService;
import me.gogradually.toycommerce.application.order.dto.CheckoutOrderInfo;
import me.gogradually.toycommerce.application.order.dto.OrderDetailInfo;
import me.gogradually.toycommerce.application.order.dto.PayOrderInfo;
import me.gogradually.toycommerce.interfaces.dto.order.CheckoutOrderResponse;
import me.gogradually.toycommerce.interfaces.dto.order.OrderDetailResponse;
import me.gogradually.toycommerce.interfaces.dto.order.PayOrderRequest;
import me.gogradually.toycommerce.interfaces.dto.order.PayOrderResponse;
import me.gogradually.toycommerce.interfaces.utils.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "주문/결제 API")
public class OrderController {

    private static final String MEMBER_ID_HEADER = "X-Member-Id";

    private final OrderService orderService;

    @PostMapping("/checkout")
    @Operation(summary = "주문 생성", description = "회원의 장바구니를 주문으로 생성하고 재고를 차감합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "주문 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 또는 주문 생성 실패",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<CheckoutOrderResponse>> checkout(
            @Parameter(description = "회원 ID 헤더", example = "1001")
            @RequestHeader(MEMBER_ID_HEADER) @Min(value = 1, message = "memberId는 1 이상이어야 합니다.") Long memberId
    ) {
        CheckoutOrderInfo info = orderService.checkout(memberId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(CheckoutOrderResponse.from(info)));
    }

    @PostMapping("/{orderId}/pay")
    @Operation(summary = "주문 결제", description = "결제를 모사하여 주문 상태를 변경합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "결제 성공/멱등 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "결제 실패 또는 잘못된 상태",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "주문 미존재",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ApiResponse<PayOrderResponse> pay(
            @Parameter(description = "회원 ID 헤더", example = "1001")
            @RequestHeader(MEMBER_ID_HEADER) @Min(value = 1, message = "memberId는 1 이상이어야 합니다.") Long memberId,
            @Parameter(description = "주문 ID", example = "1")
            @PathVariable @Min(value = 1, message = "orderId는 1 이상이어야 합니다.") Long orderId,
            @RequestBody @Valid PayOrderRequest request
    ) {
        PayOrderInfo info = orderService.pay(memberId, orderId, request.toCommand());
        return ApiResponse.success(PayOrderResponse.from(info));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "주문 상세 조회", description = "주문 상세와 항목 정보를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "주문 미존재",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ApiResponse<OrderDetailResponse> getOrder(
            @Parameter(description = "회원 ID 헤더", example = "1001")
            @RequestHeader(MEMBER_ID_HEADER) @Min(value = 1, message = "memberId는 1 이상이어야 합니다.") Long memberId,
            @Parameter(description = "주문 ID", example = "1")
            @PathVariable @Min(value = 1, message = "orderId는 1 이상이어야 합니다.") Long orderId
    ) {
        OrderDetailInfo info = orderService.getOrder(memberId, orderId);
        return ApiResponse.success(OrderDetailResponse.from(info));
    }
}
