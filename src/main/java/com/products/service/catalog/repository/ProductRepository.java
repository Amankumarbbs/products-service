package com.products.service.catalog.repository;


import com.products.service.catalog.entity.Product;
import com.products.service.catalog.response.ProductItemResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<ProductItemResponse> findByCategoryAndPriceBetween(
            final String category, final Double priceMin, final Double priceMax
    );

    default List<ProductItemResponse> findByCategoryAndPriceBetweenAndOptionalIsAvailable(
            final String category, final Double priceMin, final Double priceMax, final Pageable pageable, final Optional<Boolean> isAvailable
    ) {
        if (isAvailable.isPresent()) {
            return findByCategoryAndPriceBetweenAndIsAvailable(category, priceMin, priceMax, pageable, isAvailable.get());
        } else {
            return findByCategoryAndPriceBetween(category, priceMin, priceMax);
        }
    }

    List<ProductItemResponse> findByCategoryAndPriceBetweenAndIsAvailable(String category, Double priceMin, Double priceMax, Pageable pageable, Boolean isAvailable);
}
