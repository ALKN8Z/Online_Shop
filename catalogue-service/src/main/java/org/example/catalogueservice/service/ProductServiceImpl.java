package org.example.catalogueservice.service;

import lombok.RequiredArgsConstructor;

import org.example.catalogueservice.entity.Product;
import org.example.catalogueservice.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public Iterable<Product> getAllProducts(String filter) {
        if (filter == null || filter.isEmpty()) {
            return productRepository.findAll();
        } else {
            return productRepository.findAllByNameLikeIgnoreCase("%" + filter + "%");
        }
    }

    @Override
    @Transactional
    public Product createProduct(String name, String description) {
        return productRepository.save(new Product(null, name, description));
    }

    @Override
    public Optional<Product> getProduct(Integer productId) {
        return productRepository.findById(productId);
    }

    @Override
    @Transactional
    public void updateProduct(Integer id, String name, String description) {
        productRepository.findById(id).ifPresentOrElse(product -> {
            product.setName(name);
            product.setDescription(description);
            }, () -> {
            throw new NoSuchElementException("Такой продукт не был найден");
        });
    }

    @Override
    @Transactional
    public void deleteProduct(Product product) {
        productRepository.deleteById(product.getId());
    }
}
