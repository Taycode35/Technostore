package com.eni.technostore.service;

import com.eni.technostore.entity.Product;
import com.eni.technostore.entity.ProductType;
import com.eni.technostore.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplUnitTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void testShouldReturnAllProducts(){
        Product product1 = new Product();
        product1.setId(1L);
        product1.setProductType(ProductType.SMARTPHONE);
        product1.setBrand("Sony");
        product1.setModel("XPeria");
        product1.setPrice(600.00);
        product1.setYear(Year.of(2015));

        Product product2 = new Product();
        product2.setId(2L);
        product2.setProductType(ProductType.HEADPHONES);
        product2.setBrand("Apple");
        product2.setModel("AirPods");
        product2.setPrice(300.00);
        product2.setYear(Year.of(2022));
        when(productRepository.findAll()).thenReturn(List.of(product1, product2));

        List<Product> products = productService.findAll();

        assertThat(products)
                .isNotNull()
                .hasSize(2);

        assertThat(products.get(0).getBrand()).isEqualTo("Sony");
        assertThat(products.get(1).getBrand()).isEqualTo("Apple");

        verify(productRepository).findAll();
    }

    @Test
    void testShouldReturnProductById(){
        Product product = new Product();
        product.setId(2L);
        product.setProductType(ProductType.SMARTPHONE);
        product.setBrand("Samsung");
        product.setModel("Galaxy S21 Ultra");
        product.setPrice(990.99);
        product.setYear(Year.of(2023));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));

        Optional<Product> result = productService.findById(2L);

        assertThat(result).isPresent();
        assertThat(result.get().getBrand()).isEqualTo("Samsung");

        verify(productRepository).findById(2L);
    }

    @Test
    void testShouldReturnEmptyOptionalWhenProductDoesNotExist(){
        when(productRepository.findById(325L)).thenReturn(Optional.empty());

        Optional<Product> product = productService.findById(325L);

        assertThat(product).isEmpty();
        verify(productRepository).findById(325L);
    }

    @Test
    void testShouldReturnProductOnSaveOrUpdate(){
        Product product = new Product();
        product.setId(15L);
        product.setProductType(ProductType.SMARTWATCH);
        product.setBrand("Samsung");
        product.setModel("Watch");
        product.setPrice(271.60);
        product.setYear(Year.of(2024));
        when(productRepository.save(product)).thenReturn(product);

        Product savedProduct = productService.save(product);

        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getModel()).isEqualTo("Watch");

        verify(productRepository).save(product);
    }

    @Test
    void testShouldDeleteProduct(){
        when(productRepository.existsById(5L)).thenReturn(true);

        productService.deleteById(5L);

        verify(productRepository).deleteById(5L);
    }
}