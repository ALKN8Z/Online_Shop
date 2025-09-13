package org.example.customerapplication.client;

import org.example.customerapplication.entity.ProductReview;
import org.example.customerapplication.controller.payload.NewProductReviewPayload;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductsReviewsWebClient {
    Flux<ProductReview> findProductReviewsByProduct(Integer productId);

    Mono<ProductReview> createNewProductReview(Integer productId, NewProductReviewPayload payload);
}
