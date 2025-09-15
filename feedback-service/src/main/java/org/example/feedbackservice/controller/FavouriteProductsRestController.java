package org.example.feedbackservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.feedbackservice.entity.FavouriteProduct;
import org.example.feedbackservice.entity.payload.NewFavouriteProductPayload;
import org.example.feedbackservice.service.FavouriteProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@RequestMapping("feedback-api/favourite-products")
@RequiredArgsConstructor
@Slf4j
public class FavouriteProductsRestController {

    private final FavouriteProductService favouriteProductService;

    @GetMapping("/by-product-id/{productId:\\d+}")
    public Mono<FavouriteProduct> findFavouriteProduct(
            @PathVariable int productId,
            Mono<JwtAuthenticationToken> tokenMono) {
        return tokenMono.flatMap(token -> favouriteProductService
                .findFavouriteProduct(productId, token.getToken().getSubject()));
    }

    @GetMapping
    public Flux<FavouriteProduct> findFavouriteProducts(Mono<JwtAuthenticationToken> tokenMono) {
        return tokenMono.flatMapMany(token -> {
                log.info(token.getToken().getClaimAsString("name"));
                log.info(LocalDate.now().toString());
                return favouriteProductService.findAllFavouriteProducts(token.getToken().getSubject());});
    }

    @PostMapping
    public Mono<ResponseEntity<FavouriteProduct>> addProductToFavourites(
            @Valid @RequestBody Mono<NewFavouriteProductPayload> newFavouriteProductPayloadMono,
            UriComponentsBuilder uriComponentsBuilder,
            Mono<JwtAuthenticationToken> tokenMono) {

        return Mono.zip(tokenMono, newFavouriteProductPayloadMono)
                .flatMap((tuple -> favouriteProductService
                        .addProductToFavourites(
                                tuple.getT2().productId(),
                                tuple.getT1().getToken().getSubject())))
                .map(favouriteProduct -> ResponseEntity
                                .created(uriComponentsBuilder
                                        .replacePath("feedback-api/favourite-product/{id}")
                                        .build(favouriteProduct.getId()))
                                .body(favouriteProduct));
    }

    @DeleteMapping("by-product-id/{productId:\\d+}")
    public Mono<ResponseEntity<Void>> removeProductFromFavourites(
            @PathVariable int productId,
            Mono<JwtAuthenticationToken> tokenMono) {
        return tokenMono
                .flatMap(token -> favouriteProductService
                        .removeProductFromFavourites(productId, token.getToken().getSubject()))
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
