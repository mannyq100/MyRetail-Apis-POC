package com.myretail.productsapi.controller;

import com.myretail.productsapi.domain.Product;
import com.myretail.productsapi.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/products")
@Validated
public class ProductController {

    ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Product>> updateProduct(@RequestBody @Valid Product productRequest, @PathVariable(name = "id") String productId) {
        if (!productId.equalsIgnoreCase(productRequest.getId())) {
            throw new IllegalArgumentException("ProductId in path does not match Id in request body");
        }
        return productService.updateProduct(productRequest, productId)
                .map(ResponseEntity.ok()::body);
    }

    @PostMapping("/addProduct")
    public ResponseEntity<Flux<Product>> addProducts(@RequestBody List<@Valid Product> products) {

        Flux<Product> productFlux = productService.addProducts(products);
            return ResponseEntity.status(HttpStatus.CREATED).body(productFlux);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Product>> getProduct(@PathVariable String id) {
        return productService.getProductById(id)
                .map(ResponseEntity.ok()::body);
    }
}
