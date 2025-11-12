package com.eni.technostore.repository;

import com.eni.technostore.entity.Product;
import com.eni.technostore.entity.ProductType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@Sql("/test-data/products.sql")
class ProductRepositoryITest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testShouldFindAllProducts(){
        List<Product> products = productRepository.findAll();

        assertNotNull(products);
        assertEquals(14, products.size());
        assertEquals("Google", products.get(2).getBrand());
    }

    @Test
    void testShouldFindAllProductsByType(){
        List<Product> products = productRepository.findByProductType(ProductType.TABLET);

        assertNotNull(products);
        assertEquals(3, products.size());
        assertEquals("Microsoft", products.getLast().getBrand());
    }

    @Test
    void testShouldFindProductById(){
        Product product = productRepository.findById(3L).orElseThrow(() -> new RuntimeException("Product not found"));

        assertEquals("Google", product.getBrand());
        assertEquals("Pixel 8 Pro", product.getModel());
        assertEquals(ProductType.SMARTPHONE, product.getProductType());
    }

    @Test
    void testShouldSaveProduct(){
        Product product1 = new Product();
        product1.setProductType(ProductType.HEADPHONES);
        product1.setBrand("Bose");
        product1.setModel("C45");
        product1.setPrice(199.00);
        product1.setYear(Year.of(2016));

        Product savedProduct = productRepository.save(product1);

        assertNotNull(savedProduct);
        assertEquals(15L, savedProduct.getId().longValue());
        assertNotEquals("JBL", savedProduct.getBrand());
    }

    @Test
    void testShouldUpdateProduct(){
        Product product = productRepository.findById(11L).orElseThrow(() -> new AssertionError("Product not found"));
        assertEquals("Apple", product.getBrand());

        product.setBrand("HUAWEI");
        product.setPrice(1000.00);
        productRepository.save(product);

        assertEquals("HUAWEI", product.getBrand());
        assertEquals(1000.00, product.getPrice());
    }

    @Test
    void testShouldDeleteProduct(){
        long initialCount = productRepository.count();

        assertEquals(14, initialCount);

        productRepository.deleteById(12L);

        assertEquals(initialCount -1, productRepository.count());
        assertFalse(productRepository.existsById(12L));
    }

}