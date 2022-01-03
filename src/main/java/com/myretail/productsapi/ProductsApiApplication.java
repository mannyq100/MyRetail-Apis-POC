package com.myretail.productsapi;

import com.myretail.productsapi.domain.PriceCurrency;
import com.myretail.productsapi.domain.Product;
import com.myretail.productsapi.service.ProductService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
@Slf4j
@OpenAPIDefinition(info = @Info(title = "PRODUCT APIs", version = "1.0", description = "Documentation for Product API V1.0"))

public class ProductsApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductsApiApplication.class, args);

    }

    @Bean
    CommandLineRunner run(ProductService productService) {
        log.info("Saving initial products to database [{},{},{},{}]", 13860428, 54456119, 13264003, 12954218);
        return args -> {
            List<Product> productList = List.of(
                    Product.builder().id("13860428").current_price(new PriceCurrency(100, "USD")).build(),
                    Product.builder().id("54456119").current_price(new PriceCurrency(200, "USD")).build(),
                    Product.builder().id("13264003").current_price(new PriceCurrency(300, "USD")).build(),
                    Product.builder().id("12954218").current_price(new PriceCurrency(400, "USD")).build()
            );
            productService.addProducts(productList).blockLast();
        };
    }
}
