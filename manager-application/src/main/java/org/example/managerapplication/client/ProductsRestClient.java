package org.example.managerapplication.client;

import org.example.managerapplication.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductsRestClient {

    List<Product> getProducts(String filter);

    Optional<Product> getProduct(int productId);

    void updateProduct(int productId, String name, String description);

    Product createProduct(String name, String description);

    void deleteProduct(Product product);
}
