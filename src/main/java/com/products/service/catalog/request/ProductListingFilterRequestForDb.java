package com.products.service.catalog.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.products.service.catalog.utils.BooleanDeserializer;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class ProductListingFilterRequestForDb {
    @NotBlank(message = "Category is mandatory")
    private String category;

    @NotNull(message = "PriceMin is mandatory")
    @Min(value = 0, message = "PriceMin should be 0 or positive")
    private Double priceMin;

    @NotNull(message = "PriceMax is mandatory")
    @Min(value = 0, message = "PriceMax should be 0 or positive")
    private Double priceMax;

    @Min(value = 0, message = "Page should be 0 or positive")
    private Integer page = 0;

    @Min(value = 0, message = "Size should be 0 or positive")
    private Integer size = 0;

    @NotNull
    @JsonDeserialize(using = BooleanDeserializer.class)
    public Optional<Boolean> isAvailable = Optional.empty();
}
