package com.eni.technostore.repository;

import com.eni.technostore.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
@ActiveProfiles("test")
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @Sql("/test-data/products.sql")
    void shouldFindAllProducts(){

    /*
        Afin de mieux structuré nos TU, nous alons appliquer la règle des 3A (Arrange, Act, Assert).

     */
        // Arrange : C’est la phase de mise en place des conditions du test: on va y déclarer et initialiser les objets nécessaires.
        // Dans notre cas, le Arrange a déjà été fait lors de la préparation de nos data SQL.

        /* ----------------------- */

        // Act : C’est la phase où on exécutes l’action à tester : on appellera la méthode ou le comportement qu'on veut vérifier.
        List<Product> products = productRepository.findAll();

        /* ----------------------- */

        // Assert : C’est la phase où on constatera le résultat : on compares ce qu'on obtiens à ce qu'on attend.

        assertEquals(14, products.size());

    }

}