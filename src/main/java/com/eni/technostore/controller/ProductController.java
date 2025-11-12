//package com.eni.technostore.controller;
//
//import com.eni.technostore.entity.Product;
//import com.eni.technostore.entity.ProductType;
//import com.eni.technostore.service.ProductService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//@Controller
//@RequestMapping("/products")
//@RequiredArgsConstructor
//@Slf4j
//public class ProductController {
//
//    private final ProductService productService;
//
//    @ModelAttribute("types")
//    public ProductType[] populateTypes() {
//        return ProductType.values();
//    }
//
//    @GetMapping
//    public String index(Model model) {
//        log.info("Affichage de la liste des produits");
//        model.addAttribute("products", productService.findAll());
//        return "product-list";
//    }
//
//    @GetMapping("/new")
//    public String newProduct(Model model) {
//        log.info("Affichage du formulaire de création du produit");
//        model.addAttribute("product", new Product());
//        return "product-form";
//    }
//
//    @PostMapping("/{id}")
//    public String saveProduct(@PathVariable(required = false) Long id,
//                              @Valid @ModelAttribute("product") Product product,
//                              BindingResult bindingResult,
//                              Model model,
//                              RedirectAttributes redirectAttributes) {
//        if (bindingResult.hasErrors()) {
//            log.warn("Erreurs de validation lors de la sauvegarde du produit");
//            return "product-form";
//        }
//
//        // On détermine si c'est une création ou une mise à jour
//        boolean isNew = (id == null || id == 0);
//
//        if (!isNew) {
//            product.setId(id);
//        }
//
//        productService.save(product);
//
//        String message = isNew ? "Produit créé avec succès !" : "Produit modifié avec succès !";
//        log.info("{} : {}", isNew ? "Produit créé" : "Produit modifié", product);
//
//        redirectAttributes.addFlashAttribute("success", message);
//        return "redirect:/products";
//    }
//
//    @GetMapping("/edit/{id}")
//    public String editProduct(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
//        log.info("Affichage du formulaire d'édition pour le produit: {}", id);
//
//        try {
//            Product product = productService.findById(id)
//                    .orElseThrow(() -> new IllegalArgumentException("Produit invalide pour l'id : " + id));
//            model.addAttribute("product", product);
//            return "product-edit";
//        } catch (IllegalArgumentException e) {
//            log.error("Produit non trouvé avec l'id : {}", id);
//            redirectAttributes.addFlashAttribute("error", "Produit introuvable.");
//            return "redirect:/products";
//        }
//    }
//
//    @PutMapping("/{id}")
//    public String updateProduct(@PathVariable Long id,
//                                @Valid @ModelAttribute("product") Product product,
//                                BindingResult bindingResult,
//                                Model model,
//                                RedirectAttributes redirectAttributes) {
//        if (bindingResult.hasErrors()) {
//            log.warn("Erreurs de validation lors de la mise à jour du produit: {}", id);
//            model.addAttribute("types", ProductType.values());
//            return "product-edit";
//        }
//
//        // On s'assure que l'ID du produit correspond à celui de l'URL
//        product.setId(id);
//        productService.save(product);
//        log.info("Produit {} mis à jour avec succès.", id);
//        redirectAttributes.addFlashAttribute("redirect", "Produit mis à jour avec succès !");
//        return "redirect:/products";
//    }
//
//    @DeleteMapping("/{id}")
//    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
//        log.info("Suppression du produit : {}", id);
//
//        try {
//            productService.deleteById(id);
//            log.info("Produit {} supprimé avec succès.", id);
//            redirectAttributes.addFlashAttribute("success", "Produit supprimé avec succès !");
//        } catch (Exception e) {
//            log.error("Erreur lors de la suppression du produit {}", id, e);
//            redirectAttributes.addFlashAttribute("error", "Impossible de supprimer le produit.");
//            return "redirect:/products";
//        }
//
//        return "redirect:/products";
//    }
//}


package com.eni.technostore.controller;

import com.eni.technostore.entity.Product;
import com.eni.technostore.entity.ProductType;
import com.eni.technostore.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @ModelAttribute("types")
    public ProductType[] populateTypes() {
        return ProductType.values();
    }

    @GetMapping
    public String index(Model model) {
        log.info("Affichage de la liste des produits");
        model.addAttribute("products", productService.findAll());
        return "product-list";
    }

    @GetMapping("/new")
    public String newProduct(Model model) {
        log.info("Affichage du formulaire de création du produit");
        model.addAttribute("product", new Product());
        return "product-form";
    }

    // Endpoint séparé pour la création (POST sans ID)
    @PostMapping
    public String createProduct(@Valid @ModelAttribute("product") Product product,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.warn("Erreurs de validation lors de la création du produit");
            return "product-form";
        }

        // S'assurer que l'ID est null pour une nouvelle entité
        product.setId(null);
        productService.save(product);
        log.info("Produit créé : {}", product);
        redirectAttributes.addFlashAttribute("success", "Produit créé avec succès !");
        return "redirect:/products";
    }

    // Endpoint pour la mise à jour (POST avec ID)
    @PostMapping("/{id}")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute("product") Product product,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.warn("Erreurs de validation lors de la modification du produit: {}", id);
            model.addAttribute("types", ProductType.values());
            return "product-form";
        }

        // Vérifier si le produit existe
        if (id == null || id == 0 || !productService.existsById(id)) {
            log.error("Tentative de modification d'un produit inexistant: {}", id);
            redirectAttributes.addFlashAttribute("error", "Le produit n'existe pas.");
            return "redirect:/products";
        }

        // S'assurer que l'ID du produit correspond à celui de l'URL
        product.setId(id);
        productService.save(product);
        log.info("Produit {} mis à jour avec succès.", id);
        redirectAttributes.addFlashAttribute("success", "Produit modifié avec succès !");
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        log.info("Affichage du formulaire d'édition pour le produit: {}", id);

        try {
            Product product = productService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Produit invalide pour l'id : " + id));
            model.addAttribute("product", product);
            return "product-edit";
        } catch (IllegalArgumentException e) {
            log.error("Produit non trouvé avec l'id : {}", id);
            redirectAttributes.addFlashAttribute("error", "Produit introuvable.");
            return "redirect:/products";
        }
    }

    @PutMapping("/{id}")
    public String updateProductPut(@PathVariable Long id,
                                   @Valid @ModelAttribute("product") Product product,
                                   BindingResult bindingResult,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.warn("Erreurs de validation lors de la mise à jour du produit: {}", id);
            model.addAttribute("types", ProductType.values());
            return "product-edit";
        }

        // Vérifier si le produit existe
        if (!productService.existsById(id)) {
            log.error("Tentative de modification d'un produit inexistant: {}", id);
            redirectAttributes.addFlashAttribute("error", "Le produit n'existe pas.");
            return "redirect:/products";
        }

        // On s'assure que l'ID du produit correspond à celui de l'URL
        product.setId(id);
        productService.save(product);
        log.info("Produit {} mis à jour avec succès.", id);
        redirectAttributes.addFlashAttribute("success", "Produit modifié avec succès !");
        return "redirect:/products";
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("Suppression du produit : {}", id);

        try {
            productService.deleteById(id);
            log.info("Produit {} supprimé avec succès.", id);
            redirectAttributes.addFlashAttribute("success", "Produit supprimé avec succès !");
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du produit {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Impossible de supprimer le produit.");
            return "redirect:/products";
        }

        return "redirect:/products";
    }
}