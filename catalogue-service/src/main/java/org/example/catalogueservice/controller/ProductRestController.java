package org.example.catalogueservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.catalogueservice.controller.payload.UpdatedProductPayload;
import org.example.catalogueservice.entity.Product;
import org.example.catalogueservice.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("catalogue-api/products/{productId}")
public class ProductRestController {

    private final ProductService productService;

    @ModelAttribute("product")
    public Product product(@PathVariable("productId") int productId) {
        return productService.getProduct(productId).orElseThrow(
                () -> new NoSuchElementException("Данный товар не найден")
        );
    }

    @GetMapping()
    public Product getProduct(@ModelAttribute("product") Product product) {
        return product;
    }

    @PatchMapping()
    public ResponseEntity<?> updateProduct(@RequestBody @Valid UpdatedProductPayload payload,
                                                 BindingResult bindingResult,
                                                 @PathVariable("productId") int productId)
            throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else{
                throw new BindException(bindingResult);
            }
        } else {
            productService.updateProduct(productId, payload.name(), payload.description());
            return ResponseEntity.noContent().build();
        }
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteProduct(@ModelAttribute("product") Product product) {
        productService.deleteProduct(product);
        return ResponseEntity.noContent().build();
    }
}
