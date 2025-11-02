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
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @Sql("/test-data/products.sql")
    void shouldFindAllProductsTest(){
        List<Product> products = productRepository.findAll();
        assertNotNull(products);
        assertEquals(14, products.size());
        assertEquals("Google", products.get(2).getBrand());
    }

    @Test
    @Sql("/test-data/products.sql")
    void shouldFindAllProductsByTypeTest(){
        List<Product> products = productRepository.findByProductType(ProductType.TABLET);
        assertNotNull(products);
        assertEquals(3, products.size());
        assertEquals("Microsoft", products.getLast().getBrand());
    }

    @Test
    @Sql("/test-data/products.sql")
    void shouldFindProductByIdTest(){
        Product product = productRepository.findById(3L).orElseThrow(() -> new RuntimeException("Product not found"));
        assertEquals("Google", product.getBrand());
        assertEquals("Pixel 8 Pro", product.getModel());
        assertEquals(ProductType.SMARTPHONE, product.getProductType());
    }

    @Test
    @Sql("/test-data/products.sql")
    void shouldSaveProductTest(){
        Product product1 = new Product();
        product1.setProductType(ProductType.HEADPHONES);
        product1.setBrand("Bose");
        product1.setModel("C45");
        product1.setPrice(199.00);
        product1.setYear(Year.of(2016));

        Product savedProduct = productRepository.save(product1);
        assertNotNull(savedProduct);
        assertNotNull(savedProduct.getId());
        assertEquals(15L, savedProduct.getId().longValue());
        assertNotEquals("JBL", savedProduct.getBrand());
    }

    @Test
    @Sql("/test-data/products.sql")
    void showUpdateProductTest(){
        Product product = productRepository.findById(11L).orElseThrow(() -> new RuntimeException("Product not found"));
        assertEquals("Apple", product.getBrand());

        product.setBrand("HUAWEI");
        Product updatedProduct = productRepository.save(product);
        assertEquals("HUAWEI", updatedProduct.getBrand());

        updatedProduct.setPrice(1000.00);
        productRepository.save(updatedProduct);
        assertEquals(1000.00, updatedProduct.getPrice());
    }

    @Test
    @Sql("/test-data/products.sql")
    void shouldDeleteProductTest(){
        assertEquals(14, productRepository.count());
        productRepository.deleteById(12L);
        assertEquals(13, productRepository.count());
    }

}