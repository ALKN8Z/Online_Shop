package org.example.customerapplication.client;

import org.example.customerapplication.entity.FavouriteProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavouriteProductsWebClient {

    Mono<FavouriteProduct> getFavouriteProduct(Integer productId);

    Flux<FavouriteProduct> getAllFavouriteProducts();

    Mono<FavouriteProduct> addProductToFavourites(Integer productId);

    Mono<Void> removeProductFromFavourites(Integer productId);

}
