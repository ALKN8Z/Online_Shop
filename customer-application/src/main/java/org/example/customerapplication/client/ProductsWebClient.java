package org.example.customerapplication.client;

import org.example.customerapplication.entity.Product;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductsWebClient {
    Flux<Product> findAllProducts(String filter);
    Mono<Product> findProduct(int productId);
}
