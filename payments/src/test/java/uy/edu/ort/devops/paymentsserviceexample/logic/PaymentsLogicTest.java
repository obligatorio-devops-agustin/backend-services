package uy.edu.ort.devops.paymentsserviceexample.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uy.edu.ort.devops.paymentsserviceexample.domain.PaymentStatus;

import static org.junit.jupiter.api.Assertions.*;

class PaymentsLogicTest {

    private PaymentsLogic paymentsLogic;

    @BeforeEach
    void setUp() {
        paymentsLogic = new PaymentsLogic();
    }

    @Test
    void testPayReturnsPaymentStatus() {
        String orderId = "order123";
        PaymentStatus paymentStatus = paymentsLogic.pay(orderId);

        assertNotNull(paymentStatus);
        assertEquals(orderId, paymentStatus.getOrderId());
        assertTrue(paymentStatus.getDescription().equals("Done.") || paymentStatus.getDescription().equals("No money."));
    }

    @Test
    void testPayRandomBehavior() {
        String orderId = "order456";
        boolean successObserved = false;
        boolean failureObserved = false;

        // Simula varias ejecuciones para confirmar que ambas opciones pueden ocurrir
        for (int i = 0; i < 100; i++) {
            PaymentStatus paymentStatus = paymentsLogic.pay(orderId);
            if (paymentStatus.isSuccess()) {
                successObserved = true;
            } else {
                failureObserved = true;
            }
            if (successObserved && failureObserved) {
                break;
            }
        }

        assertTrue(successObserved, "Success case should occur at least once.");
        assertTrue(failureObserved, "Failure case should occur at least once.");
    }
}
