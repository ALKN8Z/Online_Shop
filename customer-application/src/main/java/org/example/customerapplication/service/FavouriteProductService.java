package org.example.customerapplication.service;

import org.example.customerapplication.entity.FavouriteProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavouriteProductService {
    Mono<FavouriteProduct> addProductToFavourites(int productId);

    Mono<Void> removeProductFromFavourites(int productId);

    Mono<FavouriteProduct> findFavouriteProduct(int productId);

    Flux<FavouriteProduct> findAllFavouriteProducts();
}
