package org.example.customerapplication.client;

import lombok.RequiredArgsConstructor;
import org.example.customerapplication.client.exception.ClientBadRequestException;
import org.example.customerapplication.controller.payload.NewProductReviewPayload;
import org.example.customerapplication.entity.ProductReview;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@RequiredArgsConstructor
public class ProductsReviewsWebClientImpl implements ProductsReviewsWebClient {

    private final WebClient webClient;

    @Override
    public Flux<ProductReview> findProductReviewsByProduct(Integer productId) {
        return webClient
                .get()
                .uri("feedback-api/product-reviews/by-product-id/{productId}", productId)
                .retrieve()
                .bodyToFlux(ProductReview.class);
    }

    @Override
    public Mono<ProductReview> createNewProductReview(Integer productId, NewProductReviewPayload payload) {
        return webClient
                .post()
                .uri("feedback-api/product-reviews")
                .bodyValue(new org.example.customerapplication.client.payload.NewProductReviewPayload(productId, payload.rating(), payload.content()))
                .retrieve()
                .bodyToMono(ProductReview.class)
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
}
