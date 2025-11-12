
package com.eni.technostore.controller;

import com.eni.technostore.entity.Product;
import com.eni.technostore.entity.ProductType;
import com.eni.technostore.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProductControllerUnitTest {

    @Mock
    private ProductService productService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private ProductController productController;

    private Product product;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        product = new Product();
        product.setId(1L);
        product.setBrand("Apple");
        product.setModel("iPhone 15");
        product.setPrice(1200.0);
        product.setProductType(ProductType.SMARTPHONE);
        product.setYear(Year.of(2023));
    }

    @Test
    @DisplayName("GET /products - doit retourner la vue 'product-list' avec les produits")
    void testIndex() {
        when(productService.findAll()).thenReturn(List.of(product));

        String view = productController.index(model);

        assertThat(view).isEqualTo("product-list");
        verify(model).addAttribute(eq("products"), eq(List.of(product)));
        verify(productService).findAll();
    }

    @Test
    @DisplayName("GET /products/new - doit retourner la vue 'product-form'")
    void testNewProduct() {
        String view = productController.newProduct(model);
        assertThat(view).isEqualTo("product-form");
        verify(model).addAttribute(eq("product"), any(Product.class));
    }

    @Test
    @DisplayName("POST /products - doit retourner 'product-form' en cas d'erreurs de validation (création)")
    void testCreateProductWithErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = productController.createProduct(product, bindingResult, redirectAttributes);

        assertThat(view).isEqualTo("product-form");
        verifyNoInteractions(productService);
    }

    @Test
    @DisplayName("POST /products - doit rediriger vers /products après création réussie")
    void testCreateProductSuccess() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(productService.save(any(Product.class))).thenReturn(product);

        String view = productController.createProduct(product, bindingResult, redirectAttributes);

        assertThat(view).isEqualTo("redirect:/products");
        verify(productService).save(any(Product.class));
        verify(redirectAttributes).addFlashAttribute(eq("success"), anyString());
    }

    @Test
    @DisplayName("POST /products/{id} - doit retourner 'product-form' en cas d'erreurs de validation (modification)")
    void testUpdateProductWithErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = productController.updateProduct(1L, product, bindingResult, model, redirectAttributes);

        assertThat(view).isEqualTo("product-form");
        verify(model).addAttribute(eq("types"), eq(ProductType.values()));
        verifyNoInteractions(productService);
    }

    @Test
    @DisplayName("POST /products/{id} - doit rediriger vers /products après modification réussie")
    void testUpdateProductSuccess() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(productService.existsById(1L)).thenReturn(true);
        when(productService.save(any(Product.class))).thenReturn(product);

        String view = productController.updateProduct(1L, product, bindingResult, model, redirectAttributes);

        assertThat(view).isEqualTo("redirect:/products");
        verify(productService).existsById(1L);
        verify(productService).save(product);
        verify(redirectAttributes).addFlashAttribute(eq("success"), anyString());
    }

    @Test
    @DisplayName("POST /products/{id} - doit rediriger vers /products si produit inexistant")
    void testUpdateProductNotFound() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(productService.existsById(750L)).thenReturn(false);

        String view = productController.updateProduct(750L, product, bindingResult, model, redirectAttributes);

        assertThat(view).isEqualTo("redirect:/products");
        verify(productService).existsById(750L);
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
        verify(productService, never()).save(any());
    }

    @Test
    @DisplayName("GET /products/edit/{id} - doit retourner la vue 'product-edit' si produit trouvé")
    void testEditProductSuccess() {
        when(productService.findById(1L)).thenReturn(Optional.of(product));

        String view = productController.editProduct(1L, model, redirectAttributes);

        assertThat(view).isEqualTo("product-edit");
        verify(model).addAttribute(eq("product"), eq(product));
    }

    @Test
    @DisplayName("GET /products/edit/{id} - doit rediriger vers /products si produit introuvable")
    void testEditProductNotFound() {
        when(productService.findById(1L)).thenReturn(Optional.empty());

        String view = productController.editProduct(1L, model, redirectAttributes);

        assertThat(view).isEqualTo("redirect:/products");
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
    }

    @Test
    @DisplayName("PUT /products/{id} - doit retourner 'product-edit' en cas d'erreurs de validation")
    void testUpdateProductPutWithErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = productController.updateProductPut(1L, product, bindingResult, model, redirectAttributes);

        assertThat(view).isEqualTo("product-edit");
        verify(model).addAttribute(eq("types"), eq(ProductType.values()));
        verifyNoInteractions(productService);
    }

    @Test
    @DisplayName("PUT /products/{id} - doit rediriger vers /products après mise à jour réussie")
    void testUpdateProductPutSuccess() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(productService.existsById(1L)).thenReturn(true);
        when(productService.save(any(Product.class))).thenReturn(product);

        String view = productController.updateProductPut(1L, product, bindingResult, model, redirectAttributes);

        assertThat(view).isEqualTo("redirect:/products");
        verify(productService).existsById(1L);
        verify(productService).save(product);
        verify(redirectAttributes).addFlashAttribute(eq("success"), anyString());  // "redirect" → "success"
    }

    @Test
    @DisplayName("DELETE /products/{id} - doit rediriger vers /products après suppression réussie")
    void testDeleteProductSuccess() {
        doNothing().when(productService).deleteById(1L);

        String view = productController.deleteProduct(1L, redirectAttributes);

        assertThat(view).isEqualTo("redirect:/products");
        verify(productService).deleteById(1L);
        verify(redirectAttributes).addFlashAttribute(eq("success"), anyString());
    }

    @Test
    @DisplayName("DELETE /products/{id} - doit rediriger vers /products en cas d'erreur")
    void testDeleteProductFailure() {
        doThrow(new RuntimeException("Erreur suppression"))
                .when(productService).deleteById(1L);

        String view = productController.deleteProduct(1L, redirectAttributes);

        assertThat(view).isEqualTo("redirect:/products");
        verify(productService).deleteById(1L);
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
    }
}