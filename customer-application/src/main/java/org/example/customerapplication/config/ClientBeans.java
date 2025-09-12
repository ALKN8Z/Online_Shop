package org.example.customerapplication.config;

import org.example.customerapplication.client.ProductsWebClient;
import org.example.customerapplication.client.ProductsWebClientImpl;
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
}
