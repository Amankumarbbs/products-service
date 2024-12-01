//package com.products.service.catalog;
//
//import com.products.service.catalog.config.CacheConfig;
//import com.products.service.catalog.entity.Product;
//import com.products.service.catalog.response.ProductItemResponse;
//import com.products.service.catalog.service.ProductService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import com.products.service.catalog.repository.ProductRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.mockito.Mock;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Collections;
//import java.util.List;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest(classes = { CacheConfig.class, ProductService.class})
//@ExtendWith(MockitoExtension.class)
//@AutoConfigureMockMvc
//@EnableCaching
//class ProductCatalogApplicationTests {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Mock
//    private ProductRepository repo;
//
//    @Autowired
//    private CacheManager cacheManager;
//
//    @BeforeEach
//    public void setUp() {
//        ProductItemResponse product = new ProductItemResponse(123L, "apples", 100.0,"grocery", true);
//        List<ProductItemResponse> productPage = Collections.singletonList(product);
//        Mockito.when(repo.findByCategoryAndPriceBetween(
//                       Mockito.anyString(), Mockito.anyDouble(), Mockito.anyDouble()))
//               .thenReturn(productPage);
//    }
//
//    @Test
//    public void testGetProductsByCategoryAndPrice_CacheHit() throws Exception {
//        // First request - should hit the database
//        mockMvc.perform(get("/products")
//                                .param("category", "grocery")
//                                .param("priceMin", "50")
//                                .param("priceMax", "150")
//                                .param("page", "0")
//                                .param("size", "10")
//                                .contentType(MediaType.APPLICATION_JSON))
//               .andExpect(status().isOk());
//
//        // Second request - should hit the cache
//        mockMvc.perform(get("/products")
//                                .param("category", "grocery")
//                                .param("priceMin", "50")
//                                .param("priceMax", "150")
//                                .param("page", "0")
//                                .param("size", "10")
//                                .contentType(MediaType.APPLICATION_JSON))
//               .andExpect(status().isOk());
//
//        // Verify cache content
//        assert cacheManager.getCache("products").get("grocery-50.0-150.0") != null;
//    }
//
//    @Test
//    public void testCacheInvalidation() throws Exception {
//        // First request - should hit the database
//        mockMvc.perform(get("/products")
//                                .param("category", "grocery")
//                                .param("priceMin", "50")
//                                .param("priceMax", "150")
//                                .param("page", "0")
//                                .param("size", "10")
//                                .contentType(MediaType.APPLICATION_JSON))
//               .andExpect(status().isOk());
//
//        // Simulate data change
//        ProductItemResponse product = new ProductItemResponse(123L, "apples", 101.0,"grocery", true);
//        List<ProductItemResponse> productPage = Collections.singletonList(product);
//        Mockito.when(repo.findByCategoryAndPriceBetween(
//                       Mockito.anyString(), Mockito.anyDouble(), Mockito.anyDouble()))
//               .thenReturn(productPage);
//
//        // Invalidate cache
//        cacheManager.getCache("products").evict("grocery-50.0-150.0");
//
//        // Second request - should hit the database again
//        mockMvc.perform(get("/products")
//                                .param("category", "grocery")
//                                .param("priceMin", "50")
//                                .param("priceMax", "150")
//                                .param("page", "0")
//                                .param("size", "10")
//                                .contentType(MediaType.APPLICATION_JSON))
//               .andExpect(status().isOk());
//
//        // Verify cache content
//        assert cacheManager.getCache("products").get("grocery-50.0-150.0") != null;
//    }
//
//}
