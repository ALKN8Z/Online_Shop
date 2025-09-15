package org.example.feedbackservice.service;

import org.example.feedbackservice.entity.FavouriteProduct;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavouriteProductService {

    Mono<FavouriteProduct> addProductToFavourites(int productId, String userId);

    Mono<Void> removeProductFromFavourites(int productId, String userId);

    Mono<FavouriteProduct> findFavouriteProduct(int productId, String userId);

    Flux<FavouriteProduct> findAllFavouriteProducts(String userId);
}
