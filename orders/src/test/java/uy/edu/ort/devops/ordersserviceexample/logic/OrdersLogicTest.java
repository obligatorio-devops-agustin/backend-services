package uy.edu.ort.devops.ordersserviceexample.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import uy.edu.ort.devops.ordersserviceexample.domain.OrderStatus;
import uy.edu.ort.devops.ordersserviceexample.dtos.PaymentStatus;
import uy.edu.ort.devops.ordersserviceexample.dtos.Product;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrdersLogicTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OrdersLogic ordersLogic;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBuySuccess() {
        // Mock para el servicio de productos
        when(restTemplate.getForObject(anyString(), eq(Product.class)))
                .thenReturn(new Product("product1", "Product 1", 10, "Description"));

        // Mock para el servicio de pagos
        when(restTemplate.postForObject(anyString(), eq(null), eq(PaymentStatus.class)))
                .thenReturn(new PaymentStatus("order123", true, "Payment successful"));

        // Mock para el servicio de envíos
        when(restTemplate.postForEntity(anyString(), eq(null), eq(String.class)))
                .thenReturn(null);

        // Ejecutar el método
        OrderStatus result = ordersLogic.buy(Arrays.asList("product1"));

        // Validar las aserciones
        assertTrue(result.isSuccess());
        assertEquals("Ok.", result.getDescription());
    }

    @Test
    void testBuyWithMissingProduct() {
        // Mock para el servicio de productos
        when(restTemplate.getForObject(anyString(), eq(Product.class)))
                .thenReturn(null);

        // Ejecutar el método
        OrderStatus result = ordersLogic.buy(Arrays.asList("product1"));

        // Validar las aserciones
        assertFalse(result.isSuccess());
        assertEquals("Missing: product1.", result.getDescription());
    }

    @Test
    void testBuyWithNoStock() {
        // Mock para el servicio de productos
        when(restTemplate.getForObject(anyString(), eq(Product.class)))
                .thenReturn(new Product("product1", "Product 1", 0, "Description"));

        // Ejecutar el método
        OrderStatus result = ordersLogic.buy(Arrays.asList("product1"));

        // Validar las aserciones
        assertFalse(result.isSuccess());
        assertEquals("No stock: product1.", result.getDescription());
    }

    @Test
    void testBuyPaymentFailed() {
        // Mock para el servicio de productos
        when(restTemplate.getForObject(anyString(), eq(Product.class)))
                .thenReturn(new Product("product1", "Product 1", 10, "Description"));

        // Mock para el servicio de pagos
        when(restTemplate.postForObject(anyString(), eq(null), eq(PaymentStatus.class)))
                .thenReturn(new PaymentStatus("order123", false, "Insufficient funds"));

        // Ejecutar el método
        OrderStatus result = ordersLogic.buy(Arrays.asList("product1"));

        // Validar las aserciones
        assertFalse(result.isSuccess());
        assertEquals("Insufficient funds", result.getDescription());
    }
}
