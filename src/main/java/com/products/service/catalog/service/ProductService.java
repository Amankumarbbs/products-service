package com.products.service.catalog.service;

import com.products.service.catalog.api.exceptionHandler.ProductAlreadyExistsException;
import com.products.service.catalog.entity.Product;
import com.products.service.catalog.repository.ProductRepository;
import com.products.service.catalog.dto.ProductDTO;
import com.products.service.catalog.request.ProductListingFilterRequestForDb;
import com.products.service.catalog.response.ProductItemResponse;
import com.products.service.catalog.request.ProductListingFilterRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository repository;

    @Autowired
    private CacheManager cacheManager;

    @Cacheable(value = "productsByFilter", key = "#filter.category + '_' + #filter.priceMin + '_' + #filter.priceMax", unless = "#result.isEmpty()")
    public List<ProductItemResponse> getProductsByCategoryAndPrice(ProductListingFilterRequest filter) {
        return repository.findByCategoryAndPriceBetween(
                filter.getCategory(),
                filter.getPriceMin(),
                filter.getPriceMax()
        );
    }

    public List<ProductItemResponse> getProductsByCategoryAndPriceFromDB(ProductListingFilterRequestForDb filterRequestForDb) {
        return repository.findByCategoryAndPriceBetweenAndOptionalIsAvailable(
                filterRequestForDb.getCategory(),
                filterRequestForDb.getPriceMin(),
                filterRequestForDb.getPriceMax(),
                PageRequest.of(filterRequestForDb.getPage(), filterRequestForDb.getSize()),
                filterRequestForDb.getIsAvailable()
        );
    }

    public List<ProductItemResponse> getAllProducts(Integer page, Integer size) {
        return repository.findAll(PageRequest.of(page, size)).map(ProductItemResponse::new).getContent();
    }

    @Cacheable(value = "products", key = "#id", unless = "#result == null")
    public ProductItemResponse getProductById(Long id) {
        return repository.findById(id).map(ProductItemResponse::new).orElseThrow(() -> {
            logger.debug("Product not found with id: {}", id);
            return new RuntimeException("Product not found");
        });
    }

    @CacheEvict(value = "products", key = "#id")
    @CachePut(value = "products", key = "#id")
    public ProductItemResponse updateProduct(Long id, ProductDTO productDetails) {
        Product product = repository.findById(id)
                                    .orElseThrow(() -> {
                                        logger.error("Product not found with id: {}", id);
                                        return new RuntimeException("Product not found");
                                    });
        BeanUtils.copyProperties(productDetails, product);
        return new ProductItemResponse(repository.saveAndFlush(product));
    }

    public ProductItemResponse addProduct(ProductDTO productDetails) {
        if (repository.existsById(productDetails.getId())) {
            throw new ProductAlreadyExistsException(productDetails.getId());
        }
        Product product = new Product(
                productDetails.getId(),
                productDetails.getName(),
                productDetails.getPrice(),
                productDetails.getCategory(),
                productDetails.getIsAvailable()
        );
        return new ProductItemResponse(repository.saveAndFlush(product));
    }

    @CacheEvict(value = "products", key = "#id", allEntries = true)
    public void deleteProduct(Long id) {
        if (repository.existsById(id)) {
            logger.info("Deleting product with id: {}", id);
            repository.deleteById(id);
        } else {
            logger.warn("Product with id: {} does not exist", id);
            throw new RuntimeException("Product not found");
        }
    }
}
