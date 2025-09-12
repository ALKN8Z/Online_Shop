package org.example.catalogueservice.repository;




import org.example.catalogueservice.entity.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface ProductRepository extends CrudRepository<Product, Integer> {

    @Query(name = "Product.findAllByNameLikeIgnoringCase", nativeQuery = true)
    List<Product> findAllByNameLikeIgnoreCase(@Param("filter") String filter);

//    List<Product> findAllProducts();
//
//    Product save(Product product);
//
//    Optional<Product> findById(Integer productId);
//
//    void deleteById(Integer id);
}
