package com.eni.technostore.controller;

import com.eni.technostore.entity.Product;
import com.eni.technostore.entity.ProductType;
import com.eni.technostore.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc()
@Sql("/test-data/products.sql")
class ProductControllerITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @WithAnonymousUser
    @DisplayName("GET /products - accès refusé pour utilisateur non connecté")
    void shouldDenyAccessToAnonymousUser() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    /*
     * PARTIE AFFICHAGE DES PRODUITS
     */

    @Test
    @WithMockUser(username = "user")
    @DisplayName("GET /products - autorisé pour USER")
    void shouldAllowUserToGetProducts() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("product-list"))
                .andExpect(model().attributeExists("products"));
    }

    @Test
    @WithMockUser(username = "bob", roles = {"MANAGER"})
    @DisplayName("GET /products - autorisé pour MANAGER")
    void shouldAllowManagerToGetProducts() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("product-list"))
                .andExpect(model().attributeExists("products"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("GET /products - autorisé pour ADMIN")
    void shouldAllowAdminToGetProducts() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("product-list"))
                .andExpect(model().attributeExists("products"));
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @DisplayName("GET /products/new - Affichage du formulaire de création")
    void shouldDisplayNewProductForm() throws Exception {
        mockMvc.perform(get("/products/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("product-form"))
                .andExpect(model().attributeExists("product"))
                .andExpect(model().attribute("product", instanceOf(Product.class)));
    }


    /*
     * PARTIE CRÉATION D'UN PRODUIT
     */

    @Test
    @WithMockUser(username = "bob", roles = {"MANAGER"})
    @DisplayName("POST /products - interdit pour MANAGER")
    void shouldForbidManagerToCreateProduct() throws Exception {
        mockMvc.perform(post("/products")
                        .param("brand", "Sony")
                        .param("model", "Walkman")
                        .param("productType", "HEADPHONES")
                        .param("price", "100.0")
                        .param("year", "2023")
                        .contentType("application/x-www-form-urlencoded")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST /products - autorisé pour ADMIN (création)")
    void shouldAllowAdminToCreateProduct() throws Exception {
        mockMvc.perform(post("/products")
                        .param("brand", "Bose")
                        .param("model", "QC Ultra")
                        .param("productType", "HEADPHONES")
                        .param("price", "349.99")
                        .param("year", Year.of(2025).toString())
                        .contentType("application/x-www-form-urlencoded")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"))
                .andExpect(flash().attribute("success", "Produit créé avec succès !"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST /products - Erreurs de validation lors de la création")
    void shouldReturnFormWithErrorsWhenValidationFails() throws Exception {
        mockMvc.perform(post("/products")
                        .param("brand", "")  // Champ vide provoquant une erreur de validation
                        .param("model", "")  // Champ vide ...
                        .param("productType", ProductType.SMARTPHONE.name())
                        .param("price", "0.0")  // Le prix ne doit pas être null
                        .param("year", String.valueOf(Year.of(2023).getValue()))
                        .contentType("application/x-www-form-urlencoded")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("product-form"))
                .andExpect(model().attributeHasErrors("product"))
                .andExpect(model().attributeHasFieldErrors("product", "brand"))
                .andExpect(model().attributeHasFieldErrors("product", "model"))
                .andExpect(model().attributeHasFieldErrors("product", "price"))
                .andExpect(model().attributeExists("types"));
    }

    /*
     * PARTIE MISE À JOUR D'UN PRODUIT
     */

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @DisplayName("PUT /products/{id} - autorisé pour MANAGER (modification)")
    @Transactional
    void shouldAllowManagerToUpdateProduct() throws Exception {
        // Setup - ensure product exists
        Product existingProduct = new Product();
        existingProduct.setBrand("Apple");
        existingProduct.setModel("iPhone 14");
        existingProduct.setProductType(ProductType.SMARTPHONE);
        existingProduct.setPrice(899.99);
        existingProduct.setYear(Year.of(2023));
        Product savedProduct = productRepository.save(existingProduct);

        // On force l'écriture en base et on vide le cache
        productRepository.flush();

        // Test de la mise à jour
        mockMvc.perform(put("/products/{id}", savedProduct.getId())
                        .param("brand", "Google")
                        .param("model", "Pixel 9 Pro")
                        .param("productType", ProductType.SMARTPHONE.name())
                        .param("price", "999.99")
                        .param("year", "2024")  // Use a realistic year
                        .contentType("application/x-www-form-urlencoded")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"))
                .andExpect(flash().attribute("success", "Produit modifié avec succès !"));

        // Vérification de la mise à jour réelle depuis la base
        Product updated = productRepository.findById(savedProduct.getId()).orElseThrow();
        assertThat(updated.getBrand()).isEqualTo("Google");
        assertThat(updated.getModel()).isEqualTo("Pixel 9 Pro");
        assertThat(updated.getPrice()).isEqualTo(999.99);
        assertThat(updated.getYear()).isEqualTo(Year.of(2024));
        assertThat(updated.getProductType()).isEqualTo(ProductType.SMARTPHONE);
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("POST /products/{id} - interdit pour USER")
    void shouldForbidUserToUpdateProduct() throws Exception {
        mockMvc.perform(post("/products/2")
                        .param("brand", "Samsung")
                        .param("model", "Galaxy X")
                        .param("price", "1200.0")
                        .param("productType", "SMARTPHONE")
                        .param("year", "2023")
                        .contentType("application/x-www-form-urlencoded")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST /products/{id} - Erreurs de validation lors de la modification")
    void shouldReturnFormWithErrorsOnUpdate() throws Exception {
        mockMvc.perform(post("/products/1")
                        .param("brand", "")  // Champ vide
                        .param("model", "")
                        .param("productType", ProductType.SMARTPHONE.name())
                        .param("price", "-10.0")  // Prix négatif
                        .param("year", String.valueOf(Year.of(2023).getValue()))
                        .contentType("application/x-www-form-urlencoded")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("product-form"))
                .andExpect(model().attributeHasErrors("product"))
                .andExpect(model().attributeHasFieldErrors("product", "brand"))
                .andExpect(model().attributeHasFieldErrors("product", "model"))
                .andExpect(model().attributeHasFieldErrors("product", "price"))
                .andExpect(model().attributeExists("types"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST /products/{id} - Tentative de modification d'un produit inexistant")
    void shouldRedirectWhenUpdatingNonExistentProduct() throws Exception {
        mockMvc.perform(post("/products/999")
                        .param("brand", "Apple")
                        .param("model", "iPhone X")
                        .param("productType", ProductType.SMARTPHONE.name())
                        .param("price", "999.99")
                        .param("year", String.valueOf(Year.of(2023).getValue()))
                        .contentType("application/x-www-form-urlencoded")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @DisplayName("GET /products/edit/{id} - Affichage du formulaire d'édition du produit")
    void shouldDisplayEditProductForm() throws Exception {
        mockMvc.perform(get("/products/edit/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("product-edit"))
                .andExpect(model().attributeExists("product"))
                .andExpect(model().attribute("product", instanceOf(Product.class)));
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @DisplayName("GET /products/edit/{id} - Produit introuvable")
    void shouldRedirectWhenProductNotFound() throws Exception {
        mockMvc.perform(get("/products/edit/{id}", 898L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"))
                .andExpect(flash().attributeExists("error"));
    }

    /*
     * PARTIE SUPPRESSION DES PRODUITS
     */

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("DELETE /products/{id} - autorisé pour ADMIN")
    void shouldAllowAdminToDeleteProduct() throws Exception {
        mockMvc.perform(delete("/products/5")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @DisplayName("DELETE /products/{id} - interdit pour MANAGER")
    void shouldForbidManagerToDeleteProduct() throws Exception {
        mockMvc.perform(delete("/products/5")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("DELETE /products/{id} - redirection vers login pour utilisateur anonyme")
    void shouldRedirectAnonymousOnDelete() throws Exception {
        mockMvc.perform(delete("/products/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("DELETE /products/{id} - Suppression réussie")
    void shouldAllowAdminToDeleteProductSuccess() throws Exception {
        mockMvc.perform(delete("/products/{id}", 2L)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"))
                .andExpect(flash().attributeExists("success"));
    }
}