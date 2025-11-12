package com.eni.technostore.repository;

import com.eni.technostore.entity.Product;
import com.eni.technostore.entity.ProductType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Year;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ProductRepositoryUnitTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("Test findByProductType")
    void testFindByProductType() {
        Product p = new Product();
        p.setProductType(ProductType.SMARTPHONE);
        p.setBrand("Sony");
        p.setModel("XPERIA");
        p.setPrice(500.0);
        p.setProductYear(Year.of(2015));
        productRepository.save(p);

        List<Product> products = productRepository.findByProductType(ProductType.SMARTPHONE);
        assertThat(products).isNotEmpty();
        assertThat(products).contains(p);
    }

    @Test
    @DisplayName("Test findByBrandIgnoreCase")
    void testFindByBrandIgnoreCase() {
        Product p1 = new Product();
        p1.setProductType(ProductType.LAPTOP);
        p1.setBrand("HP");
        p1.setModel("Omen");
        p1.setPrice(650.0);
        p1.setProductYear(Year.of(2020));
        productRepository.save(p1);

        Product p2 = new Product();
        p2.setProductType(ProductType.LAPTOP);
        p2.setBrand("HP");
        p2.setModel("Omnibook Ultra Flip 14");
        p2.setPrice(1299.0);
        p2.setProductYear(Year.of(2025));
        productRepository.save(p2);

        List<Product> products = productRepository.findByBrandIgnoreCase("hp");
        assertThat(products).isNotEmpty();
        assertThat(products.get(1).getModel()).isEqualToIgnoringCase("Omnibook Ultra Flip 14");
    }
}
