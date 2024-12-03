package uy.edu.ort.devops.shippingserviceexample.endpoints;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ShippingEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCreateShipping() throws Exception {
        mockMvc.perform(post("/shipping/{id}", "d")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetShippingExists() throws Exception {
        mockMvc.perform(get("/shipping/{id}", "a")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Delivered"))
                .andExpect(jsonPath("$.id").value("a"));
    }

    @Test
    void testGetShippingNotExists() throws Exception {
        mockMvc.perform(get("/shipping/{id}", "z")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
