// ProductDTO.java
package com.products.service.catalog.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.products.service.catalog.utils.BooleanDeserializer;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    @NotNull(message = "Id is Required")
    @Positive(message = "Id should be positive")
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Size(min = 1, max = 255)
    private String name;

    @NotNull(message = "Price is mandatory")
    @Min(value = 0, message = "Price should be 0 or positive")
    private Double price;

    @NotBlank(message = "Category is mandatory")
    @Size(min = 1, max = 255)
    private String category;

    @NotNull(message = "Availability is mandatory")
    @JsonDeserialize(using = BooleanDeserializer.class)
    private Boolean isAvailable;
}