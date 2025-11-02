package com.eni.technostore.service;

import com.eni.technostore.entity.Product;
import com.eni.technostore.entity.ProductType;

import java.util.List;
import java.util.Optional;
public interface ProductService {

    /**
     * Récupère tous les produits.
     * @return Liste de tous les produits
     */
    List<Product> findAll();

    /**
     * Récupère un produit par son identifiant.
     * @param id Identifiant du produit
     * @return Optional contenant le produit s'il existe
     */
    Optional<Product> findById(Long id);

    /**
     * Crée ou met à jour un produit.
     * @param product Produit à sauvegarder
     * @return Produit sauvegardé avec son ID généré
     */
    Product save(Product product);

    /**
     * Supprime un produit par son identifiant.
     * @param id Identifiant du produit à supprimer
     * @throws IllegalArgumentException si le produit n'existe pas
     */
    void deleteById(Long id);

    /**
     * Recherche des produits par type.
     * @param productType Type de produit recherché
     * @return Liste des produits correspondants
     */
    List<Product> findByProductType(ProductType productType);

    /**
     * Recherche des produits par marque.
     * @param brand Marque recherchée
     * @return Liste des produits correspondants
     */
    List<Product> findByBrand(String brand);

    /**
     * Vérifie si un produit existe.
     * @param id Identifiant du produit
     * @return true si le produit existe, false sinon
     */
    boolean existsById(Long id);

    /**
     * Compte le nombre total de produits.
     * @return Nombre de produits
     */
    long count();
}
