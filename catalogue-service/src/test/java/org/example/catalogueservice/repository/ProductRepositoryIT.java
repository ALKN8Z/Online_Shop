package org.example.catalogueservice.repository;

import org.example.catalogueservice.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Sql("/sql/test-products.sql")
public class ProductRepositoryIT {
    @Autowired
    private ProductRepository productRepository;

    @Test
    public void findAllByNameLikeIgnoreCase_shouldReturnAllProductsFilteredByName() {
        List<Product> result = productRepository.findAllByNameLikeIgnoreCase("%Test%");

        assertThat(result).isEqualTo(List.of(
                new Product(2, "test", "test"),
                new Product(3, "testProduct", "testDescription")));
    }
}
