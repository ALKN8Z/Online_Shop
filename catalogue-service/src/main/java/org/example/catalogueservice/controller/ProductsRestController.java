package org.example.catalogueservice.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.catalogueservice.controller.payload.NewProductPayload;
import org.example.catalogueservice.entity.Product;
import org.example.catalogueservice.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("catalogue-api/products")
public class ProductsRestController {
    private final ProductService productService;

    @GetMapping
    public Iterable<Product> getProducts(@RequestParam(name = "filter", required = false) String filter) {
       return productService.getAllProducts(filter);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@RequestBody @Valid NewProductPayload payload,
                                           UriComponentsBuilder uriBuilder,
                                           BindingResult bindingResult)
            throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception){
                throw exception;
            } else{
                throw new BindException(bindingResult);
            }

        } else{
            Product newProduct = productService.createProduct(payload.name(), payload.description());
            return ResponseEntity
                    .created(uriBuilder
                            .replacePath("catalogue-api/products/{productId}")
                            .build(Map.of("productId", newProduct.getId())))
                    .body(newProduct);
        }
    }
}
