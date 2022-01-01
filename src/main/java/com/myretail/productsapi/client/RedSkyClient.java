package com.myretail.productsapi.client;

import com.myretail.productsapi.domain.productDescription.ProductInfoResponse;
import com.myretail.productsapi.exception.ProductNotFoundException;
import com.myretail.productsapi.exception.RedSkyClientException;
import com.myretail.productsapi.exception.RedSkyServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@Slf4j
public class RedSkyClient {

    private final WebClient webClient;

    @Value("${redSkyServer.key}")
    private String productNameKey;

    public RedSkyClient(@Autowired WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<ProductInfoResponse> getProductInfo(String productId) {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("key", productNameKey)
                        .queryParam("tcin", productId).build())

                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, (clientResponse -> {
                    log.error("RedSkyClient Status code : {}", clientResponse.statusCode().value());
                    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.error(new ProductNotFoundException("Product Name does not exist for productId: " + productId));
                    }
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(response -> Mono.error(new RedSkyClientException(response, clientResponse.statusCode().value())));
                }))
                .onStatus(HttpStatus::is5xxServerError, (clientResponse -> {
                    log.info("RedSkyClient Status code : {}", clientResponse.statusCode().value());
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(response -> Mono.error(new RedSkyServerException(response, clientResponse.statusCode().value())));
                }))
                .bodyToMono(ProductInfoResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(3))
                        .filter(throwable -> throwable instanceof RedSkyServerException)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                            throw new RedSkyServerException("External Service failed to process after max retries", HttpStatus.SERVICE_UNAVAILABLE.value());
                        }));
    }
}
