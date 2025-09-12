package org.example.customerapplication.client;

import org.example.customerapplication.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ProductsWebClientImpl implements ProductsWebClient {

    private final WebClient webClient;

    @Override
    public Flux<Product> findAllProducts(String filter) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/catalogue-api/products")
                        .queryParam("filter", filter)
                        .build())
                .retrieve()
                .bodyToFlux(Product.class)
                .doOnError(Throwable::printStackTrace);
    }

    @Override
    public Mono<Product> findProduct(int productId) {
        return webClient
                .get()
                .uri("/catalogue-api/products/{productId}", productId)
                .retrieve()
                .bodyToMono(Product.class)
                .onErrorComplete(WebClientResponseException.NotFound.class);

    }
}
