package me.gogradually.toycommerce.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WebCorsConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldAllowPreflightForPutRequestFromFrontendOrigin() throws Exception {
        mockMvc.perform(options("/api/orders/1/details")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "PUT")
                        .header("Access-Control-Request-Headers", "Content-Type,X-Member-Id"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
                .andExpect(header().string("Access-Control-Allow-Methods", org.hamcrest.Matchers.containsString("PUT")));
    }
}
