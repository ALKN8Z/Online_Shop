package org.example.feedbackservice.service;

import lombok.RequiredArgsConstructor;
import org.example.feedbackservice.entity.FavouriteProduct;
import org.example.feedbackservice.repository.FavouriteProductRepository;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FavouriteProductServiceImpl implements FavouriteProductService {

    private final FavouriteProductRepository favouriteProductRepository;

    @Override
    public Mono<FavouriteProduct> addProductToFavourites(int productId, String userId) {
        return favouriteProductRepository.save(new FavouriteProduct(UUID.randomUUID(), productId, userId));
    }

    @Override
    public Mono<Void> removeProductFromFavourites(int productId, String userId) {
        return favouriteProductRepository.deleteByProductIdAndUserId(productId, userId);
    }

    @Override
    public Mono<FavouriteProduct> findFavouriteProduct(int productId, String userId) {
        return favouriteProductRepository.findByProductIdAndUserId(productId, userId);
    }

    @Override
    public Flux<FavouriteProduct> findAllFavouriteProducts(String userId) {
        return favouriteProductRepository.findAllByUserId(userId);
    }

}
