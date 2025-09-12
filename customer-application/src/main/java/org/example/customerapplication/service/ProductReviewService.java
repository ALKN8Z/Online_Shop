package org.example.customerapplication.service;

import org.example.customerapplication.entity.ProductReview;
import org.example.customerapplication.entity.payload.NewProductReviewPayload;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductReviewService {
    Mono<ProductReview> createNewProductReview(int productId, NewProductReviewPayload payload);

    Flux<ProductReview> findProductReviewsByProduct(int productId);
}
