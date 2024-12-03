package uy.edu.ort.devops.shippingserviceexample.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uy.edu.ort.devops.shippingserviceexample.domain.Shipping;

import static org.junit.jupiter.api.Assertions.*;

class ShippingLogicTest {

    private ShippingLogic shippingLogic;

    @BeforeEach
    void setUp() {
        shippingLogic = new ShippingLogic();
    }

    @Test
    void testHasShipping() {
        assertTrue(shippingLogic.hasShipping("a"));
        assertFalse(shippingLogic.hasShipping("z"));
    }

    @Test
    void testGetShipping() {
        Shipping shipping = shippingLogic.getShipping("a");
        assertNotNull(shipping);
        assertEquals("Delivered", shipping.getStatus());
        assertEquals("a", shipping.getId());
    }

    @Test
    void testAddShipping() {
        shippingLogic.addShipping("d");
        // Since addShipping doesn't modify the map, no additional assertions are needed here.
    }
}
