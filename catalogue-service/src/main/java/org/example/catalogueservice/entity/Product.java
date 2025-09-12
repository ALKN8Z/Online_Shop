package org.example.catalogueservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "catalogue", name = "t_product")

//Именованные запросы
@NamedQueries({
        @NamedQuery(
                name = "Product.findAllByNameLikeIgnoringCase",
                query = "select p from Product p where p.name ilike :filter"
        )
})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "c_name", nullable = false)
    @Size(min = 3, max = 50)
    private String name;

    @Column(name = "c_description", nullable = false)
    @Size(max = 1000)
    private String description;
}
