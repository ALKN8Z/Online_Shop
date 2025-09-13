package org.example.customerapplication.config;

import lombok.RequiredArgsConstructor;
import org.example.customerapplication.client.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientBeans {

    @Bean
    public ProductsWebClient productsWebClient(
            @Value("${product-shop.services.catalogue.uri:http://localhost:8081}") String catalogueBaseUrl)
    {
        return new ProductsWebClientImpl(WebClient.builder()
                .baseUrl(catalogueBaseUrl)
                .build());
    }

    @Bean
    public FavouriteProductsWebClient favouriteProductsWebClient(
            @Value("${product-shop.services.feedback.uri:http://localhost:8084}") String feedbackBaseUrl)
    {
        return new FavouriteProductsWebClientImpl(WebClient.builder()
                .baseUrl(feedbackBaseUrl)
                .build());
    }

    @Bean
    public ProductsReviewsWebClient productsReviewsWebClient(
            @Value("${product-shop.services.feedback.uri:http://localhost:8084}") String feedbackBaseUrl)
    {
        return new ProductsReviewsWebClientImpl(WebClient.builder()
                .baseUrl(feedbackBaseUrl)
                .build());
    }
}
