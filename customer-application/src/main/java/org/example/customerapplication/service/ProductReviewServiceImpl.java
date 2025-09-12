package org.example.customerapplication.service;

import lombok.RequiredArgsConstructor;
import org.example.customerapplication.entity.ProductReview;
import org.example.customerapplication.entity.payload.NewProductReviewPayload;
import org.example.customerapplication.repository.ProductReviewRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductReviewServiceImpl implements ProductReviewService {

    private final ProductReviewRepository productReviewRepository;

    @Override
    public Mono<ProductReview> createNewProductReview(int productId, NewProductReviewPayload payload) {
        return productReviewRepository.save(
                new ProductReview(
                        UUID.randomUUID(),
                        productId,
                        payload.rating(),
                        payload.content()));
    }

    @Override
    public Flux<ProductReview> findProductReviewsByProduct(int productId) {
        return productReviewRepository.findAllByProductId(productId);
    }
}
