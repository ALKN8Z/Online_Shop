package org.example.feedbackservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.feedbackservice.entity.ProductReview;
import org.example.feedbackservice.entity.payload.NewProductReviewPayload;
import org.example.feedbackservice.service.ProductReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("feedback-api/product-reviews")
@RequiredArgsConstructor
public class ProductReviewsRestController {

    private final ProductReviewService productReviewService;

    @GetMapping("by-product-id/{productId:\\d+}")
    public Flux<ProductReview> getProductReviewsByProduct(@PathVariable int productId) {
        return productReviewService.findProductReviewsByProduct(productId);
    }

    @PostMapping
    public Mono<ResponseEntity<ProductReview>> createProductReview(
            @Valid @RequestBody Mono<NewProductReviewPayload> newProductReviewPayloadMono,
            UriComponentsBuilder uriComponentsBuilder){

        return newProductReviewPayloadMono
                .flatMap(newProductReviewPayload -> productReviewService.createNewProductReview(
                        newProductReviewPayload.productId(), newProductReviewPayload))
                .map(productReview -> ResponseEntity
                        .created(uriComponentsBuilder
                                .replacePath("feedback-api/product-reviews/{id}")
                                .build(productReview.getId()))
                        .body(productReview));
    }


}
