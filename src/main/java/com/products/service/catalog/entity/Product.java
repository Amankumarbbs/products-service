package com.products.service.catalog.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Product {

    @Id
    @JsonProperty(value = "id")
    @Column(nullable = false, name = "id")
    private Long id;

    @JsonProperty(value = "name")
    @Column(name = "name")
    private String name;

    @JsonProperty(value = "price")
    @Column(name = "price")
    private Double price;

    @JsonProperty(value = "category")
    @Column(name = "category")
    private String category;

    @JsonProperty(value = "isAvailable")
    @Column(name = "isAvailable")
    private Boolean isAvailable;

    @Version
    private int version;

    public Product(final Long id, final String name, final Double price, final String category, final Boolean isAvailable) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.isAvailable = isAvailable;
    }
}