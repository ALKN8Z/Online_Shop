package org.example.customerapplication.config;

import lombok.RequiredArgsConstructor;
import org.example.customerapplication.client.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientBeans {

    @Bean
    @Scope("prototype")
    public WebClient.Builder myWebClientBuilder(
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            ServerOAuth2AuthorizedClientRepository authorizedClientRepository
    ) {

        ServerOAuth2AuthorizedClientExchangeFilterFunction filter =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(
                        clientRegistrationRepository,
                        authorizedClientRepository);

        filter.setDefaultClientRegistrationId("keycloak");

        return WebClient.builder().filter(filter);
    }

    @Bean
    public ProductsWebClient productsWebClient(
            @Value("${product-shop.services.catalogue.uri:http://localhost:8081}") String catalogueBaseUrl,
            WebClient.Builder myWebClientBuilder)
    {
        return new ProductsWebClientImpl(myWebClientBuilder
                .baseUrl(catalogueBaseUrl)
                .build());
    }

    @Bean
    public FavouriteProductsWebClient favouriteProductsWebClient(
            @Value("${product-shop.services.feedback.uri:http://localhost:8084}") String feedbackBaseUrl,
            WebClient.Builder myWebClientBuilder)
    {
        return new FavouriteProductsWebClientImpl(myWebClientBuilder
                .baseUrl(feedbackBaseUrl)
                .build());
    }

    @Bean
    public ProductsReviewsWebClient productsReviewsWebClient(
            @Value("${product-shop.services.feedback.uri:http://localhost:8084}") String feedbackBaseUrl,
            WebClient.Builder myWebClientBuilder)
    {
        return new ProductsReviewsWebClientImpl(myWebClientBuilder
                .baseUrl(feedbackBaseUrl)
                .build());
    }
}
