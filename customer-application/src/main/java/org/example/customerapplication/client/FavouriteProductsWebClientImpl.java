package org.example.customerapplication.client;

import lombok.RequiredArgsConstructor;
import org.example.customerapplication.client.exception.ClientBadRequestException;
import org.example.customerapplication.client.payload.NewFavouriteProductPayload;
import org.example.customerapplication.entity.FavouriteProduct;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@RequiredArgsConstructor
public class FavouriteProductsWebClientImpl implements FavouriteProductsWebClient {

    private final WebClient webClient;

    @Override
    public Mono<FavouriteProduct> getFavouriteProduct(Integer productId) {
        return webClient
                .get()
                .uri("/feedback-api/favourite-products/by-product-id/{productId}", productId)
                .retrieve()
                .bodyToMono(FavouriteProduct.class)
                .onErrorComplete(WebClientResponseException.NotFound.class);
    }

    @Override
    public Flux<FavouriteProduct> getAllFavouriteProducts() {
        return webClient
                .get()
                .uri("/feedback-api/favourite-products")
                .retrieve()
                .bodyToFlux(FavouriteProduct.class);
    }

    @Override
    public Mono<FavouriteProduct> addProductToFavourites(Integer productId) {
        return webClient
                .post()
                .uri("/feedback-api/favourite-products")
                .bodyValue(new NewFavouriteProductPayload(productId))
                .retrieve()
                .bodyToMono(FavouriteProduct.class)
                .onErrorMap(WebClientResponseException.BadRequest.class, ex ->{
                    ProblemDetail problem = ex.getResponseBodyAs(ProblemDetail.class);

                    List<String> errors = null;
                    if (problem != null && problem.getProperties() != null) {
                        Object rawErrors = problem.getProperties().get("errors");
                        if (rawErrors instanceof List<?>) {
                            errors = ((List<?>) rawErrors).stream()
                                    .filter(String.class::isInstance)
                                    .map(String.class::cast)
                                    .toList();
                        }
                    }
                    return new ClientBadRequestException(ex, errors != null ? errors : List.of("Некорректный запрос"));
                });
    }

    @Override
    public Mono<Void> removeProductFromFavourites(Integer productId) {
        return webClient
                .delete()
                .uri("/feedback-api/favourite-products/by-product-id/{productId}")
                .retrieve()
                .toBodilessEntity()
                .then();
    }
}
