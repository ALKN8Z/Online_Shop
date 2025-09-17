package org.example.customerapplication.config;

import org.example.customerapplication.client.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

import static org.mockito.Mockito.mock;

@Configuration
public class TestBeans {

    @Bean
    public ReactiveClientRegistrationRepository clientRegistrationRepository(){
        return mock();
    }

    @Bean
    public ServerOAuth2AuthorizedClientRepository authorizedClientRepository(){
        return mock();
    }

    @Bean
    @Primary
    public ProductsWebClient mockProductsWebClient() {
        return new ProductsWebClientImpl(WebClient.builder()
                .baseUrl("http://localhost:54321")
                .build());
    }

    @Bean
    @Primary
    public FavouriteProductsWebClient testFavouriteProductsWebClient() {
        return new FavouriteProductsWebClientImpl(WebClient.builder()
                .baseUrl("http://localhost:54321")
                .build());
    }

    @Bean
    @Primary
    public ProductsReviewsWebClient testProductsReviewsWebClient() {
        return new ProductsReviewsWebClientImpl(WebClient.builder()
                .baseUrl("http://localhost:54321")
                .build());
    }
}
