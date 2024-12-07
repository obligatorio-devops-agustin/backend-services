package uy.edu.ort.devops.ordersserviceexample.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uy.edu.ort.devops.ordersserviceexample.domain.OrderStatus;
import uy.edu.ort.devops.ordersserviceexample.dtos.PaymentStatus;
import uy.edu.ort.devops.ordersserviceexample.dtos.Product;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class OrdersLogic {

    private static Logger logger = LoggerFactory.getLogger(OrdersLogic.class);

    @Value("${PAYMENTS_URL}")
    private String paymentsServiceURL;

    @Value("${SHIPPING_URL}")
    private String shippingServiceURL;

    @Value("${PRODUCTS_URL}")
    private String productsServiceURL;

    @Autowired
    private RestTemplate restTemplate;

    public String health (){
        StringBuilder result = new StringBuilder();
        result.append("Payments: ");
        result.append(paymentsServiceURL);
        result.append(" Shipping: ");
        result.append(shippingServiceURL);
        result.append(" Products: ");
        result.append(productsServiceURL);
        try {
            result.append(restTemplate.getForObject(productsServiceURL + "/products/" + 111, String.class));
        } catch (HttpClientErrorException ex)   {
            result.append(" Products service is down.");
            result.append(ex.getMessage());
            result.append(" ------ ");
            result.append(Arrays.toString(ex.getStackTrace()));
        }

        return result.toString();

    }

    public OrderStatus buy(List<String> products) {
        StringBuilder errorBuilder = new StringBuilder();
        logger.info("Creating order.");
        logger.info("Checking products.");

        boolean hasError = false;
        for (String productId : products) {
            Product product = getProduct(productId);
            if (product != null) {
                if (product.getStock() == 0) {
                    if (hasError) {
                        errorBuilder.append(" ");
                    }
                    hasError = true;
                    errorBuilder.append("No stock: ").append(productId).append(".");
                }
            } else {
                if (hasError) {
                    errorBuilder.append(" ");
                }
                hasError = true;
                errorBuilder.append("Missing: ").append(productId).append(".");
            }

        }

        String orderId = UUID.randomUUID().toString();
        if (!hasError) {
            logger.info("Products ok.");
            PaymentStatus paymentStatus = pay(orderId);
            if (paymentStatus.isSuccess()) {
                logger.info("Payment ok.");
                addShipping(orderId);
                return new OrderStatus(orderId, true, "Ok.");
            } else {
                logger.info("Error in payment: " + paymentStatus.getDescription());
                return new OrderStatus(orderId, false, paymentStatus.getDescription());
            }
        } else {
            String productErrors = errorBuilder.toString();
            logger.info("Error in products: " + productErrors);
            return new OrderStatus(orderId, false, productErrors);
        }
    }

    private Product getProduct(String id) {
        try {
            logger.info("Invoking products service.");
            return restTemplate.getForObject(productsServiceURL + "/products/" + id, Product.class);
        } catch (HttpClientErrorException ex)   {
            return null;
        }
    }

    private PaymentStatus pay(String orderId) {
        try {
            logger.info("Invoking payments service.");
            return restTemplate.postForObject(paymentsServiceURL + "/payments/" + orderId, null, PaymentStatus.class);
        } catch (HttpClientErrorException ex)   {
            return null;
        }
    }

    private void addShipping(String orderId) {
        logger.info("Invoking shipping service.");
        restTemplate.postForEntity(shippingServiceURL + "/shipping/" + orderId, null, String.class);
    }
}