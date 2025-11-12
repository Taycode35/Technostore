package com.eni.technostore.service;

import com.eni.technostore.entity.Product;
import com.eni.technostore.entity.ProductType;
import com.eni.technostore.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@Sql("/test-data/products.sql")
class ProductServiceImplITest {

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private ProductRepository productRepository;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setup() {
        product1 = new Product();
        product1.setProductType(ProductType.HEADPHONES);
        product1.setBrand("Bose");
        product1.setModel("QuietComfort Ultra");
        product1.setPrice(349.99);
        product1.setYear(Year.of(2024));

        product2 = new Product();
        product2.setProductType(ProductType.LAPTOP);
        product2.setBrand("HP");
        product2.setModel("Omen");
        product2.setPrice(650.0);
        product2.setYear(Year.of(2020));
    }

    @Test
    @DisplayName("findAll() - doit retourner tous les produits")
    void testFindAllProducts() {
        List<Product> products = productService.findAll();

        assertThat(products)
                .isNotNull()
                .isNotEmpty()
                .extracting(Product::getBrand)
                .contains("Google", "Apple", "Sony");
    }

    @Test
    @DisplayName("findById() - doit retourner un produit existant")
    void testFindProductByIdExisting() {
        Optional<Product> productOpt = productService.findById(3L);

        assertThat(productOpt).isPresent();
        assertThat(productOpt.get().getBrand()).isEqualTo("Google");
        assertThat(productOpt.get().getModel()).isEqualTo("Pixel 8 Pro");
    }

    @Test
    @DisplayName("findById() - doit retourner vide si le produit n'existe pas")
    void testFindProductByIdNotFound() {
        Optional<Product> productOpt = productService.findById(999L);
        assertThat(productOpt).isEmpty();
    }

    @Test
    @DisplayName("save() - doit créer un nouveau produit")
    void testSaveNewProduct() {
        long initialCount = productRepository.count();

        Product savedProduct1 = productService.save(product1);
        Product savedProduct2 = productService.save(product2);

        assertThat(savedProduct1.getId()).isNotNull();
        assertThat(savedProduct2.getId()).isNotNull();
        assertThat(savedProduct1.getBrand()).isEqualTo("Bose");
        assertThat(savedProduct2.getBrand()).isEqualTo("HP");
        assertThat(productRepository.count()).isEqualTo(initialCount + 2);
    }

    @Test
    @DisplayName("save() - doit mettre à jour un produit existant")
    void testUpdateExistingProduct() {
        Product existingProduct = productService.findById(11L)
                .orElseThrow(() -> new AssertionError("Produit non trouvé"));

        existingProduct.setBrand("Huawei");
        existingProduct.setPrice(899.99);

        Product updatedProduct = productService.save(existingProduct);

        assertThat(updatedProduct.getBrand()).isEqualTo("Huawei");
        assertThat(updatedProduct.getPrice()).isEqualTo(899.99);
    }

    @Test
    @DisplayName("deleteById() - doit supprimer un produit existant")
    void testDeleteProduct() {
        long initialCount = productRepository.count();

        productService.deleteById(12L);

        assertThat(productRepository.existsById(12L)).isFalse();
        assertThat(productRepository.count()).isEqualTo(initialCount - 1);
    }

    @Test
    @DisplayName("deleteById() - doit lever une exception si le produit n'existe pas")
    void testDeleteNonExistingProductThrowsException() {
        long initialCount = productRepository.count();

        assertThatThrownBy(() -> productService.deleteById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("n'existe pas");

        assertThat(productRepository.count()).isEqualTo(initialCount);
    }

    @Test
    @DisplayName("findByProductType() - doit retourner les produits du type TABLET")
    void testFindByProductType() {
        List<Product> tablets = productService.findByProductType(ProductType.TABLET);

        assertThat(tablets)
                .isNotNull()
                .isNotEmpty()
                .allMatch(p -> p.getProductType() == ProductType.TABLET);
    }

    @Test
    @DisplayName("findByBrand() - doit retourner les produits de la marque Apple (ignore case)")
    void testFindProductByBrand() {
        List<Product> appleProducts = productService.findByBrand("apple");

        assertThat(appleProducts)
                .isNotEmpty()
                .allMatch(p -> p.getBrand().equalsIgnoreCase("Apple"));
    }

    @Test
    @DisplayName("existsById() - doit retourner vrai pour un produit existant")
    void testExistsProductByIdTrue() {
        assertThat(productService.existsById(1L)).isTrue();
    }

    @Test
    @DisplayName("existsById() - doit retourner faux pour un produit inexistant")
    void testExistsProductByIdFalse() {
        assertThat(productService.existsById(999L)).isFalse();
    }

    @Test
    @DisplayName("count() - doit retourner le nombre total de produits")
    void testCount() {
        long repoCount = productRepository.count();
        long serviceCount = productService.count();

        assertThat(serviceCount).isEqualTo(repoCount);
    }
}
