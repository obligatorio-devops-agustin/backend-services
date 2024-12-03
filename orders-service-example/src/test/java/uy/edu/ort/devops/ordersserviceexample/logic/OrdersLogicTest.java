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

        // Set URLs for dependent services
        OrdersLogic.setPaymentsServiceUrl("http://localhost:8081");
        OrdersLogic.setShippingServiceUrl("http://localhost:8082");
        OrdersLogic.setProductsServiceUrl("http://localhost:8083");
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
                .thenReturn(null); // Simula que el servicio de envíos no devuelve nada relevante

        // Ejecutar el método
        OrderStatus result = ordersLogic.buy(Arrays.asList("product1"));

        // Validar las aserciones
        assertTrue(result.isSuccess());
        assertEquals("Ok.", result.getDescription());
    }

    @Test
    void testBuyWithMissingProduct() {
        // Mock products service
        when(restTemplate.getForObject("http://localhost:8083/products/product1", Product.class))
                .thenReturn(null);

        // Execute
        OrderStatus result = ordersLogic.buy(Arrays.asList("product1"));

        // Assertions
        assertFalse(result.isSuccess());
        assertEquals("Missing: product1.", result.getDescription());
    }

    @Test
    void testBuyWithNoStock() {
        // Mock products service
        when(restTemplate.getForObject("http://localhost:8083/products/product1", Product.class))
                .thenReturn(new Product("product1", "Product 1", 0, "Description"));

        // Execute
        OrderStatus result = ordersLogic.buy(Arrays.asList("product1"));

        // Assertions
        assertFalse(result.isSuccess());
        assertEquals("No stock: product1.", result.getDescription());
    }

    @Test
    void testBuyPaymentFailed() {
        // Mock products service
        when(restTemplate.getForObject("http://localhost:8083/products/product1", Product.class))
                .thenReturn(new Product("product1", "Product 1", 10, "Description"));

        // Mock payments service
        when(restTemplate.postForObject("http://localhost:8081/payments/order123", null, PaymentStatus.class))
                .thenReturn(new PaymentStatus("order123", false, "Payment failed"));

        // Execute
        OrderStatus result = ordersLogic.buy(Arrays.asList("product1"));

        // Assertions
        assertFalse(result.isSuccess());
        assertEquals("Payment failed", result.getDescription());
    }
}
