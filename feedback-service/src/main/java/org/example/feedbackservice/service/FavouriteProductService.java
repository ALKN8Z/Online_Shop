package org.example.feedbackservice.service;

import org.example.feedbackservice.entity.FavouriteProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavouriteProductService {

    Mono<FavouriteProduct> addProductToFavourites(int productId);

    Mono<Void> removeProductFromFavourites(int productId);

    Mono<FavouriteProduct> findFavouriteProduct(int productId);

    Flux<FavouriteProduct> findAllFavouriteProducts();
}
