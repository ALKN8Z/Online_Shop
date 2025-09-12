package org.example.managerapplication.controller;

import lombok.RequiredArgsConstructor;
import org.example.managerapplication.client.ProductsRestClient;
import org.example.managerapplication.controller.payload.NewProductPayload;
import org.example.managerapplication.exception.BadRequestException;
import org.example.managerapplication.model.Product;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.logging.Logger;

@Controller
@RequestMapping("catalogue/products")
@RequiredArgsConstructor
public class ProductsController {

    private final ProductsRestClient productsRestClient;

    @GetMapping("/list")
    public String getProducts(Model model, @RequestParam(name = "filter", required = false) String filter) {
        model.addAttribute("products", productsRestClient.getProducts(filter));
        model.addAttribute("filter", filter);
        return "catalogue/products/list";
    }

    @GetMapping("/create")
    public String createProduct() {
        return "catalogue/products/create";
    }

    @PostMapping("/create")
    public String createProduct(NewProductPayload payload, Model model) {
        try {
            Product product = productsRestClient.createProduct(payload.name(), payload.description());
            return "redirect:/catalogue/products/%d".formatted(product.id());
        } catch (BadRequestException e) {
            model.addAttribute("payload", payload);
            model.addAttribute("errors", e.getErrors());
            return "catalogue/products/create";
        }
    }


}
