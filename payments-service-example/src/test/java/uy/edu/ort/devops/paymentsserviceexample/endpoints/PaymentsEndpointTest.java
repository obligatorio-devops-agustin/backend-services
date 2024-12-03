package uy.edu.ort.devops.paymentsserviceexample.endpoints;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentsEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testPayEndpointReturnsPaymentStatus() throws Exception {
        String orderId = "order123";

        mockMvc.perform(post("/payments/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId))
                .andExpect(jsonPath("$.success").isBoolean())
                .andExpect(jsonPath("$.description").isNotEmpty());
    }
}
