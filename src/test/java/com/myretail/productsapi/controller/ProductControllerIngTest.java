package com.myretail.productsapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myretail.productsapi.client.RedSkyClient;
import com.myretail.productsapi.domain.PriceCurrency;
import com.myretail.productsapi.domain.Product;
import com.myretail.productsapi.domain.productDescription.ProductInfoResponse;
import com.myretail.productsapi.repo.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ImportAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
@ActiveProfiles("test")
@AutoConfigureWebClient
@TestPropertySource(properties = {
        "redSkyServer.url=http//localhost:4047/v1",
        "redSkyServer.key=apiKey"
})
 class ProductControllerIngTest {

    private static final String PRODUCTS_BASE_URL = "v1/products";
    @Autowired
    ProductRepository productRepository;
    @Autowired
    WebTestClient webTestClient;
    @MockBean
    RedSkyClient redSkyClient;

    @BeforeEach
    void setUp() {
        var products = List.of(
                new Product("28374947", "Electric Gigstand", new PriceCurrency(10.3, "USD")),
                new Product("28374357", "Acoustic Gigstand", new PriceCurrency(100.00, "USD"))
        );
        productRepository.saveAll(products).blockLast();

    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll().block();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void addProductsTest() {
        //given
        Product product = new Product("28374937", "The Big Lebowski (Blu-ray) (Widescreen)", new PriceCurrency(20.5, "USD"));

        //when
        webTestClient.post()
                .uri(PRODUCTS_BASE_URL + "/addProduct")
                .bodyValue(Arrays.asList(product))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Product[].class)
                .consumeWith(productEntityExchangeResult -> {
                    Product[] responseBody = productEntityExchangeResult.getResponseBody();
                    assert responseBody != null;
                //    assert responseBody[0].getId().equals("28374937");
                });
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getProductByIdTest() throws IOException {
        //given
        String productId = "28374357";

        var resource = ProductControllerIngTest.class.getResource("/productDescription.json");
        var productInfoResponse =Mono.just(new ObjectMapper().readValue(resource, ProductInfoResponse.class));
        given(this.redSkyClient.getProductInfo(productId)).willReturn(productInfoResponse);

        //when
        webTestClient.get()
                .uri(PRODUCTS_BASE_URL + "/{id}", productId)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Product.class)
                .consumeWith(productEntityExchangeResult -> {
                    Product responseBody = productEntityExchangeResult.getResponseBody();
                    assert responseBody != null;
                    assert responseBody.getName().equals("Replaced Name");
                    assert responseBody.getId().equals(productId);
                });
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateProductTest() {
        //given
        String productId = "28374947";
        Product productWithUpdatePrice = new Product("28374947", "Acoustic Gigstand", new PriceCurrency(500.00, "USD"));

        //when
        webTestClient.put()
                .uri(PRODUCTS_BASE_URL + "/{id}", productId)
                .bodyValue(productWithUpdatePrice)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Product.class)
                .consumeWith(productEntityExchangeResult -> {
                    Product updatedProduct = productEntityExchangeResult.getResponseBody();
                    assert updatedProduct != null;
                    assert updatedProduct.getCurrent_price().getValue() == 500.00;
                });
    }
}