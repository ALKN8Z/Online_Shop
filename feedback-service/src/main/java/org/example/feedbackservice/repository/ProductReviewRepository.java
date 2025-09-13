package org.example.feedbackservice.repository;

import org.example.feedbackservice.entity.ProductReview;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProductReviewRepository extends ReactiveCrudRepository<ProductReview, UUID> {
    Flux<ProductReview> findAllByProductId(int productId);
}
