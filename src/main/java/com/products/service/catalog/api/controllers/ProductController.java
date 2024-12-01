package com.products.service.catalog.api.controllers;

import com.products.service.catalog.dto.ProductDTO;
import com.products.service.catalog.request.ProductListingFilterRequest;
import com.products.service.catalog.request.ProductListingFilterRequestForDb;
import com.products.service.catalog.response.DataResponse;
import com.products.service.catalog.response.ProductItemResponse;
import com.products.service.catalog.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/filter")
    public ResponseEntity<DataResponse<List<ProductItemResponse>>> getProductsByCategoryAndPrice(
            @Valid @RequestBody ProductListingFilterRequest filter
    ) {
        List<ProductItemResponse> products = productService.getProductsByCategoryAndPrice(filter);
        return ResponseEntity.ok(new DataResponse<> (products));
    }

    @PostMapping("/db/filter")
    public ResponseEntity<DataResponse<List<ProductItemResponse>>> getProductsByCategoryAndPriceFromDB(
            @Valid @RequestBody ProductListingFilterRequestForDb filter
    ) {
        List<ProductItemResponse> products = productService.getProductsByCategoryAndPriceFromDB(filter);
        return ResponseEntity.ok(new DataResponse<>(products));
    }

    @GetMapping("/all")
    public ResponseEntity<DataResponse<List<ProductItemResponse>>> getAllProducts(
            @RequestParam Integer page,
            @RequestParam Integer size
    ) {
        List<ProductItemResponse> products = productService.getAllProducts(page, size);
        return ResponseEntity.ok(new DataResponse<>(products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResponse<ProductItemResponse>> getProductById(@PathVariable Long id) {
        ProductItemResponse product = productService.getProductById(id);
        return ResponseEntity.ok(new DataResponse<>(product));
    }

    @PostMapping("/add")
    public ResponseEntity<DataResponse<ProductItemResponse>> addProduct(@Valid @RequestBody ProductDTO product) {
        ProductItemResponse productItemResponse = productService.addProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(new DataResponse<>(productItemResponse));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<DataResponse<ProductItemResponse>> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO productDetails) {
        ProductItemResponse productItemResponse = productService.updateProduct(id, productDetails);
        return ResponseEntity.ok(new DataResponse<>(productItemResponse));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}