package com.eni.technostore.controller;

import com.eni.technostore.model.Product;
import com.eni.technostore.model.ProductType;
import com.eni.technostore.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @GetMapping("/")
    public String index(Model model) {
        log.info("Affichage de la liste des produits");
        model.addAttribute("products", productService.findAll());
        return "index";
    }

    @GetMapping("/product/new")
    public String newProduct(Model model) {
        log.info("Affichage du formulaire de création de produit");
        model.addAttribute("product", new Product());
        model.addAttribute("types", ProductType.values());
        return "product-form";
    }

    @PostMapping("/product")
    public String saveProduct(@Valid @ModelAttribute("product") Product product, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            log.warn("Erreurs de validation lors de la création du produit");
            model.addAttribute("types", ProductType.values());
            return "product-form";
        }

        productService.save(product);
        log.info("Produit créé avec succès");
        return "redirect:/";
    }

    @GetMapping("/product/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        log.info("Affichage du formulaire d'édition pour le produit: {}", id);

        Product product = productService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid product id: " + id));

        model.addAttribute("product", product);
        model.addAttribute("types", ProductType.values());
        return "product-edit";
    }

    @PostMapping("/product/{id}")
    public String updateProduct(@PathVariable Long id, @Valid @ModelAttribute("product") Product product, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            log.warn("Erreurs de validation lors de la mise à jour du produit: {}", id);
            model.addAttribute("types", ProductType.values());
            return "product-edit";
        }
        // S'assurer que l'ID du produit correspond à celui de l'URL
        product.setId(id);
        productService.save(product);
        log.info("Produit mis à jour avec succès: {}", id);
        return "redirect:/";
    }

    @GetMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        log.info("Suppression du produit: {}", id);
        productService.deleteById(id);
        return "redirect:/";
    }
}