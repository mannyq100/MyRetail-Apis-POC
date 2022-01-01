package com.myretail.productsapi.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ApplicationConfig {

    @Value("${redSkyServer.url}")
    private String productNameUrl;

    @Bean
    public WebClient webClient() {
        return
                WebClient.builder()
                        .baseUrl(productNameUrl)
                        .build();
    }

}
