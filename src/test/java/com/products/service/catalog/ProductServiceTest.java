package com.products.service.catalog;

import com.products.service.catalog.api.controllers.ProductController;
import com.products.service.catalog.dto.ProductDTO;
import com.products.service.catalog.request.ProductListingFilterRequest;
import com.products.service.catalog.request.ProductListingFilterRequestForDb;
import com.products.service.catalog.response.DataResponse;
import com.products.service.catalog.response.ProductItemResponse;
import com.products.service.catalog.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetProductsByCategoryAndPrice() {
        ProductListingFilterRequest filter = new ProductListingFilterRequest();
        filter.setCategory("grocery");
        filter.setPriceMin(50.0);
        filter.setPriceMax(150.0);

        List<ProductItemResponse> products = Collections.singletonList(new ProductItemResponse());
        when(productService.getProductsByCategoryAndPrice(filter)).thenReturn(products);

        ResponseEntity<DataResponse<List<ProductItemResponse>>> response = productController.getProductsByCategoryAndPrice(filter);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(products, response.getBody().getData());
        verify(productService, times(1)).getProductsByCategoryAndPrice(filter);
    }

    @Test
    void testGetProductsByCategoryAndPriceFromDB() {
        ProductListingFilterRequestForDb filter = new ProductListingFilterRequestForDb();
        filter.setCategory("grocery");
        filter.setPriceMin(50.0);
        filter.setPriceMax(150.0);

        List<ProductItemResponse> products = Collections.singletonList(new ProductItemResponse());
        when(productService.getProductsByCategoryAndPriceFromDB(filter)).thenReturn(products);

        ResponseEntity<DataResponse<List<ProductItemResponse>>> response = productController.getProductsByCategoryAndPriceFromDB(filter);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(products, response.getBody().getData());
        verify(productService, times(1)).getProductsByCategoryAndPriceFromDB(filter);
    }

    @Test
    void testGetProductById() {
        Long productId = 1L;
        ProductItemResponse product = new ProductItemResponse();
        when(productService.getProductById(productId)).thenReturn(product);

        ResponseEntity<DataResponse<ProductItemResponse>> response = productController.getProductById(productId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(product, response.getBody().getData());
        verify(productService, times(1)).getProductById(productId);
    }

    @Test
    void testaddProduct() {
        ProductDTO product = new ProductDTO();
        ProductItemResponse productItemResponse = new ProductItemResponse();
        when(productService.addProduct(product)).thenReturn(productItemResponse);

        ResponseEntity<DataResponse<ProductItemResponse>> response = productController.addProduct(product);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(productItemResponse, Objects.requireNonNull(response.getBody()).getData());
        verify(productService, times(1)).addProduct(product);
    }

    @Test
    void testUpdateProduct() {
        Long productId = 1L;
        ProductDTO product = new ProductDTO();
        ProductItemResponse productItemResponse = new ProductItemResponse();
        when(productService.updateProduct(productId, product)).thenReturn(productItemResponse);

        ResponseEntity<DataResponse<ProductItemResponse>> response = productController.updateProduct(productId, product);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(productItemResponse, Objects.requireNonNull(response.getBody()).getData());
        verify(productService, times(1)).updateProduct(productId, product);
    }

    @Test
    void testDeleteProduct() {
        Long productId = 1L;
        doNothing().when(productService).deleteProduct(productId);

        ResponseEntity<Void> response = productController.deleteProduct(productId);

        assertEquals(204, response.getStatusCodeValue());
        verify(productService, times(1)).deleteProduct(productId);
    }
}
