package org.example.customerapplication.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.customerapplication.client.ProductsWebClient;
import org.example.customerapplication.entity.Product;
import org.example.customerapplication.entity.payload.NewProductReviewPayload;
import org.example.customerapplication.service.FavouriteProductService;
import org.example.customerapplication.service.ProductReviewService;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("customer/products/{productId:\\d+}")
@RequiredArgsConstructor
public class ProductController {
    private final ProductsWebClient productsWebClient;

    private final FavouriteProductService favouriteProductService;

    private final ProductReviewService productReviewService;

    @ModelAttribute(name = "product", binding = false)
    public Mono<Product> loadProduct(@PathVariable int productId) {
        return productsWebClient.findProduct(productId)
                .switchIfEmpty(Mono.error(new NoSuchElementException("customer.products.error.not_found")));
    }

    @ModelAttribute(name = "inFavourite", binding = false)
    public Mono<Boolean> inFavouriteProduct(@PathVariable int productId) {
        return favouriteProductService.findFavouriteProduct(productId)
                .map(favouriteProduct -> true)
                .defaultIfEmpty(false);
    }

    @GetMapping
    public Mono<String> getProduct(Model model, @PathVariable int productId,
                                   @ModelAttribute("inFavourite") boolean inFavourite) {

        model.addAttribute("inFavourite", inFavourite);

        return productReviewService.findProductReviewsByProduct(productId)
                .collectList()
                .doOnNext(productReviews -> model.addAttribute("reviews", productReviews))
                .thenReturn("customer/products/product");
    }

    @PostMapping("/add-favourite-product")
    public Mono<String> addFavouriteProduct(@ModelAttribute("product") Mono<Product> productMono) {
        return productMono
                .map(Product::id)
                .flatMap(productId -> favouriteProductService.addProductToFavourites(productId)
                        .thenReturn("redirect:/customer/products/%d".formatted(productId)));
    }

    @PostMapping("/remove-favourite-product")
    public Mono<String> removeFavouriteProduct(@ModelAttribute("product") Mono<Product> productMono) {
        return productMono
                .map(Product::id)
                .flatMap(productId -> favouriteProductService.removeProductFromFavourites(productId)
                        .thenReturn("redirect:/customer/products/%d".formatted(productId)));
    }

    @PostMapping("/create-review")
    public Mono<String> createReview(@PathVariable int productId,
                                     @Valid NewProductReviewPayload payload,
                                     BindingResult bindingResult,
                                     Model model,
                                     @ModelAttribute("inFavourite") boolean inFavourite) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("inFavourite", inFavourite);
            model.addAttribute("payload", payload);
            model.addAttribute("errors", bindingResult.getAllErrors()
                    .stream().map(ObjectError::getDefaultMessage).toList());
            return Mono.just("customer/products/product");
        } else {
            return productReviewService.createNewProductReview(productId, payload)
                    .thenReturn("redirect:/customer/products/%d".formatted(productId));
        }
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "customer/products/errors/404";
    }

}
