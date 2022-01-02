package com.myretail.productsapi.service;

import com.myretail.productsapi.client.RedSkyClient;
import com.myretail.productsapi.domain.Product;
import com.myretail.productsapi.domain.productDescription.*;
import com.myretail.productsapi.exception.ProductNotFoundException;
import com.myretail.productsapi.repo.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProductService {
    ProductRepository productRepository;
    RedSkyClient redSkyClient;

    @Autowired
    public ProductService(ProductRepository productRepository,
                          RedSkyClient redSkyClient) {
        this.productRepository = productRepository;
        this.redSkyClient = redSkyClient;
    }

    public Flux<Product> addProducts(List<Product> products) {
        log.info("In product Service. Adding products to database {}", products);
        return productRepository.saveAll(products);
    }

    public Mono<Product> getProductById(String id) {
        log.info("In product Service. Retrieving product {} from database", id);
        return productRepository.findById(id).
                switchIfEmpty(Mono.error(new ProductNotFoundException("Product with Id: " + id + " does not exist. ")))
                .flatMap(this::buildProductToReturn);
    }

    private Mono<Product> buildProductToReturn(Product product) {
        log.info("In product service. In product Service. Getting info for productId: {}", product.getId());
        var productInfo = redSkyClient.getProductInfo(product.getId());
        log.info("In product service. Product resolved from redSky api: {}", productInfo);

        Optional<String> productName = productInfo.blockOptional()
                .map(ProductInfoResponse::getData)
                .map(ProductInfo::getProduct)
                .map(ProductItem::getItem)
                .map(Item::getProductDescription)
                .map(ProductDescription::getTitle);


        return Mono.just(Product.builder().
                id(product.getId())
                .name(productName.isPresent() ? productName.get() : product.getName())
                .current_price(product.getCurrent_price())
                .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Mono<Product> updateProduct(Product productRequest, String productId) {

        return productRepository.findById(productId).
                switchIfEmpty(Mono.error(new ProductNotFoundException("Product with Id: " + productId + " does not exist. Update not possible"))).flatMap(product -> {
            product.setName(productRequest.getName());
            product.setCurrent_price(productRequest.getCurrent_price());
            return productRepository.save(product);
        });
    }


}
