package uy.edu.ort.devops.productsserviceexample.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uy.edu.ort.devops.productsserviceexample.domain.Product;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class ProductsLogicTest {

    private ProductsLogic productsLogic;

    @BeforeEach
    void setUp() {
        productsLogic = new ProductsLogic();
    }

    @Test
    void testList() {
        Collection<Product> products = productsLogic.list();
        assertNotNull(products);
        assertEquals(3, products.size());
    }

    @Test
    void testGetProduct() {
        Product product = productsLogic.getProduct("123");
        assertNotNull(product);
        assertEquals("123", product.getId());
        assertEquals("Producto 123", product.getName());
    }

    @Test
    void testGetProductNotFound() {
        Product product = productsLogic.getProduct("999");
        assertNull(product);
    }

    @Test
    void testHasProduct() {
        assertTrue(productsLogic.hasProduct("123"));
        assertFalse(productsLogic.hasProduct("999"));
    }
}
