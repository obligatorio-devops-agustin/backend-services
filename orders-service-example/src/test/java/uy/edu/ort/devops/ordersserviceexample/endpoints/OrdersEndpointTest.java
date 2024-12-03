package uy.edu.ort.devops.ordersserviceexample.endpoints;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uy.edu.ort.devops.ordersserviceexample.logic.OrdersLogic;
import uy.edu.ort.devops.ordersserviceexample.domain.OrderStatus;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrdersEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrdersLogic ordersLogic;

    @Test
    void testBuySuccess() throws Exception {
        // Configurar el comportamiento del mock de OrdersLogic
        when(ordersLogic.buy(Collections.singletonList("product1")))
                .thenReturn(new OrderStatus("order123", true, "Ok."));

        // Ejecutar el endpoint y validar la respuesta
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"product1\"]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("order123"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.description").value("Ok."));
    }
}
