package org.example.feedbackservice.service;

import org.example.feedbackservice.entity.ProductReview;
import org.example.feedbackservice.entity.payload.NewProductReviewPayload;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductReviewService {
    Mono<ProductReview> createNewProductReview(int productId, NewProductReviewPayload payload);

    Flux<ProductReview> findProductReviewsByProduct(int productId);

}
