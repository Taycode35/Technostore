package com.eni.technostore.repository;

import com.eni.technostore.entity.Product;
import com.eni.technostore.entity.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByProductType(ProductType productType);

    List<Product> findByBrandIgnoreCase(String brand);
}
