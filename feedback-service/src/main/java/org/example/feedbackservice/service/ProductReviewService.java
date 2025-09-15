package org.example.feedbackservice.service;

import org.example.feedbackservice.entity.ProductReview;
import org.example.feedbackservice.entity.payload.NewProductReviewPayload;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface ProductReviewService {
    Mono<ProductReview> createNewProductReview(int productId, NewProductReviewPayload payload,
                                               String userId, String ownerName, LocalDateTime createdAt);

    Flux<ProductReview> findProductReviewsByProduct(int productId);

}
