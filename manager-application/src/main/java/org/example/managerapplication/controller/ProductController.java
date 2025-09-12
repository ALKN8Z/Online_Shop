package org.example.managerapplication.controller;

import lombok.RequiredArgsConstructor;
import org.example.managerapplication.client.ProductsRestClient;
import org.example.managerapplication.controller.payload.UpdatedProductPayload;
import org.example.managerapplication.exception.BadRequestException;
import org.example.managerapplication.model.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("/catalogue/products/{productId:\\d+}")
@RequiredArgsConstructor
public class ProductController {
    private final ProductsRestClient productsRestClient;

    @ModelAttribute("product")
    public Product product(@PathVariable("productId") int productId) {
        return productsRestClient.getProduct(productId).orElseThrow(() ->
                new NoSuchElementException("Товар с таким id не найден"));
    }

    @GetMapping
    public String getProduct() {
        return "catalogue/products/product";
    }

    @GetMapping("/edit")
    public String editProductPage() {
        return "catalogue/products/edit";
    }

    @PostMapping("/edit")
    public String editProduct(@ModelAttribute(value = "product", binding = false) Product product,
                              UpdatedProductPayload payload,
                              Model model) {
        try {
            productsRestClient.updateProduct(product.id(), payload.name(), payload.description());
            return "redirect:/catalogue/products/%d".formatted(product.id());
        } catch (BadRequestException exception){
            model.addAttribute("payload", payload);
            model.addAttribute("errors", exception.getErrors());
            return "catalogue/products/edit";
        }
    }

    @PostMapping("/delete")
    public String deleteProduct(@ModelAttribute("product") Product product) {
        productsRestClient.deleteProduct(product);
        return "redirect:/catalogue/products/list";
    }
}
