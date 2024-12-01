package com.products.service.catalog;

import com.products.service.catalog.config.CacheConfig;
import com.products.service.catalog.dto.ProductDTO;
import com.products.service.catalog.entity.Product;
import com.products.service.catalog.repository.ProductRepository;
import com.products.service.catalog.response.ProductItemResponse;
import com.products.service.catalog.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.context.ContextConfiguration;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {CacheConfig.class, ProductCatalogApplication.class})
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@EnableCaching
@ContextConfiguration(classes = {ProductService.class, CacheConfig.class})
class ProductServiceCacheTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        RedisConnectionFactory redisConnectionFactory = mock(RedisConnectionFactory.class);
        cacheManager = new RedisCacheManager(
                RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory),
                RedisCacheConfiguration.defaultCacheConfig()
        );

        productService = new ProductService();
        Field repositoryField = ProductService.class.getDeclaredField("repository");
        repositoryField.setAccessible(true);
        repositoryField.set(productService, repository);

        Field cacheManagerField = ProductService.class.getDeclaredField("cacheManager");
        cacheManagerField.setAccessible(true);
        cacheManagerField.set(productService, cacheManager);
    }

    @Test
    void testGetProductById_Cacheable() {
        Long productId = 1L;

        // Create mock product and response
        Product product = new Product();
        product.setId(productId);
        ProductItemResponse productItemResponse = new ProductItemResponse(product);

        // Mock repository behavior
        when(repository.findById(productId)).thenReturn(Optional.of(product));

        // First call should hit the repository
        ProductItemResponse response1 = productService.getProductById(productId);
        assertEquals(productItemResponse.getId(), response1.getId());
        verify(repository, times(1)).findById(productId);

        // Second call should hit the cache (no additional call to repository)
        ProductItemResponse response2 = productService.getProductById(productId);
        assertEquals(productItemResponse.getId(), response2.getId());
        verify(repository, times(1)).findById(productId); // Verify repository is called only once

        // Check cache contents
        Cache cache = cacheManager.getCache("products");
        assertNotNull(cache);
        Cache.ValueWrapper cachedValue = cache.get(productId);
        assertNotNull(cachedValue);
        assertEquals(productItemResponse, cachedValue.get());
    }

    @Test
    void testUpdateProduct_CacheEvictAndCachePut() {
        Long productId = 1L;
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Updated Product");
        Product product = new Product();
        product.setId(productId);
        product.setName("Original Product");

        Product updatedProduct = new Product();
        updatedProduct.setId(productId);
        updatedProduct.setName("Updated Product");

        ProductItemResponse expectedResponse = new ProductItemResponse(updatedProduct);

        when(repository.findById(productId)).thenReturn(Optional.of(product));
        when(repository.saveAndFlush(any(Product.class))).thenReturn(updatedProduct);

        // Update product should evict and put cache
        ProductItemResponse actualResponse = productService.updateProduct(productId, productDTO);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        verify(repository, times(1)).findById(productId);
        verify(repository, times(1)).saveAndFlush(any(Product.class));
    }

    @Test
    void testDeleteProduct_CacheEvict() {
        Long productId = 1L;

        when(repository.existsById(productId)).thenReturn(true);
        doNothing().when(repository).deleteById(productId);

        // Delete product should evict cache
        productService.deleteProduct(productId);
        verify(repository, times(1)).existsById(productId);
        verify(repository, times(1)).deleteById(productId);
    }
}
