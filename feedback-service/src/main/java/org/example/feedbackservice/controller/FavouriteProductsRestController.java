package org.example.feedbackservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.feedbackservice.entity.FavouriteProduct;
import org.example.feedbackservice.entity.payload.NewFavouriteProductPayload;
import org.example.feedbackservice.service.FavouriteProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("feedback-api/favourite-products")
@RequiredArgsConstructor
public class FavouriteProductsRestController {

    private final FavouriteProductService favouriteProductService;

    @GetMapping("/by-product-id/{productId:\\d+}")
    public Mono<FavouriteProduct> findFavouriteProduct(@PathVariable int productId) {
        return favouriteProductService.findFavouriteProduct(productId);
    }

    @GetMapping
    public Flux<FavouriteProduct> findFavouriteProducts() {
        return favouriteProductService.findAllFavouriteProducts();
    }

    @PostMapping
    public Mono<ResponseEntity<FavouriteProduct>> addProductToFavourites(
            @Valid @RequestBody Mono<NewFavouriteProductPayload> newFavouriteProductPayloadMono,
            UriComponentsBuilder uriComponentsBuilder) {

        return newFavouriteProductPayloadMono
                .flatMap(product -> favouriteProductService.addProductToFavourites(product.productId()))
                .map(favouriteProduct -> ResponseEntity
                                .created(uriComponentsBuilder
                                        .replacePath("feedback-api/favourite-product/{id}")
                                        .build(favouriteProduct.getId()))
                                .body(favouriteProduct));
    }

    @DeleteMapping("by-product-id/{productId:\\d+}")
    public Mono<ResponseEntity<Void>> removeProductFromFavourites(@PathVariable int productId) {
        return favouriteProductService.removeProductFromFavourites(productId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
