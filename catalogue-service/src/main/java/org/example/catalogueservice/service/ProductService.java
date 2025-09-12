package org.example.catalogueservice.service;



import org.example.catalogueservice.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Iterable<Product> getAllProducts(String filter);

    Product createProduct(String name, String description);

    Optional<Product> getProduct(Integer productId);

    void updateProduct(Integer id, String name, String description);

    void deleteProduct(Product product);
}
