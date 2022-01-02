package com.myretail.productsapi.controller;

import com.myretail.productsapi.domain.Product;
import com.myretail.productsapi.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Update price for this product. Admin access required.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Price updated"),
            @ApiResponse(responseCode = "400", description = "productIds mismatch"),
            @ApiResponse(responseCode = "404", description = "Product Not Found to be updated")
    })
    public Mono<ResponseEntity<Product>> updateProduct(@RequestBody @Valid Product productRequest, @PathVariable(name = "id") String productId) {
        if (!productId.equalsIgnoreCase(productRequest.getId())) {
            throw new IllegalArgumentException("ProductId in path does not match Id in request body");
        }
        return productService.updateProduct(productRequest, productId)
                .map(ResponseEntity.ok()::body);
    }

    @PostMapping("/addProduct")
    @Operation(summary = "Add product to database. Admin access required.")
    public ResponseEntity<Flux<Product>> addProducts(@RequestBody List<@Valid Product> products) {

        Flux<Product> productFlux = productService.addProducts(products);
        return ResponseEntity.status(HttpStatus.CREATED).body(productFlux);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product Not Found")
    })
    public Mono<ResponseEntity<Product>> getProduct(@PathVariable String id) {
        return productService.getProductById(id)
                .map(ResponseEntity.ok()::body);
    }
}
