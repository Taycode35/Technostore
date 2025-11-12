package com.eni.technostore.service;

import com.eni.technostore.entity.Product;
import com.eni.technostore.entity.ProductType;
import com.eni.technostore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<Product> findAll() {
        log.debug("Récupération de tous les produits");
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> findById(Long id) {
        log.debug("Recherche du produit avec l'ID: {}", id);
        return productRepository.findById(id);
    }

    @Override
    @Transactional
    public Product save(Product product) {
        log.info("Sauvegarde du produit: {}", product);

        validateProduct(product);

        Product savedProduct = productRepository.save(product);
        log.info("Produit sauvegardé avec l'ID: {}", savedProduct.getId());

        return savedProduct;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Suppression du produit avec l'ID: {}", id);

        // Vérification de l'existence avant suppression
        if (!productRepository.existsById(id)) {
            log.error("Tentative de suppression d'un produit inexistant: {}", id);
            throw new IllegalArgumentException("Le produit avec l'ID " + id + " n'existe pas");
        }

        productRepository.deleteById(id);
        log.info("Produit supprimé avec succès: {}", id);
    }

    @Override
    public List<Product> findByProductType(ProductType productType) {
        log.debug("Recherche des produits de type: {}", productType);
        return productRepository.findByProductType(productType);
    }

    @Override
    public List<Product> findByBrand(String brand) {
        log.debug("Recherche des produits de marque: {}", brand);
        return productRepository.findByBrandIgnoreCase(brand);
    }

    @Override
    public boolean existsById(Long id) {
        return productRepository.existsById(id);
    }

    @Override
    public long count() {
        log.debug("Compte le nombre total de produits");
        return productRepository.count();
    }

    private void validateProduct(Product product) {
        if (product.getPrice() != null && product.getPrice() > 50000) {
            log.warn("Prix très élevé détecté: {}", product.getPrice());
        }
    }
}