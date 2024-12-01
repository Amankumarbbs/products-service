package com.products.service.catalog.response;


import com.products.service.catalog.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductItemResponse {

    private Long id;
    private String name;
    private Double price;
    private String category;
    private Boolean isAvailable;

    public ProductItemResponse(final Product product) {
        this.category = product.getCategory();
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.isAvailable = product.getIsAvailable();
    }
}
