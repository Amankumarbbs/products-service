package com.products.service.catalog.api.exceptionHandler;

import lombok.Getter;

@Getter
public class ProductAlreadyExistsException extends RuntimeException {
    private final Long productId;

    public ProductAlreadyExistsException(Long productId) {
        super("Product with ID " + productId + " already exists.");
        this.productId = productId;
    }

}
