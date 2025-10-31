package com.eni.technostore.repository;

import com.eni.technostore.model.Product;
import com.eni.technostore.model.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByProductType(ProductType productType);

    List<Product> findByBrandIgnoreCase(String brand);
}
