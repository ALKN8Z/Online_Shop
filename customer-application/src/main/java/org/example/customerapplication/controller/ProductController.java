package org.example.customerapplication.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.customerapplication.client.FavouriteProductsWebClient;
import org.example.customerapplication.client.ProductsReviewsWebClient;
import org.example.customerapplication.client.ProductsWebClient;
import org.example.customerapplication.client.exception.ClientBadRequestException;
import org.example.customerapplication.entity.Product;
import org.example.customerapplication.controller.payload.NewProductReviewPayload;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("customer/products/{productId:\\d+}")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductsWebClient productsWebClient;

    private final FavouriteProductsWebClient favouriteProductsWebClient;

    private final ProductsReviewsWebClient productsReviewsWebClient;

    @ModelAttribute(name = "product", binding = false)
    public Mono<Product> loadProduct(@PathVariable int productId) {
        return productsWebClient.findProduct(productId)
                .switchIfEmpty(Mono.error(new NoSuchElementException("customer.products.error.not_found")));
    }

    @ModelAttribute(name = "inFavourite", binding = false)
    public Mono<Boolean> inFavouriteProduct(@PathVariable int productId) {
        return favouriteProductsWebClient.getFavouriteProduct(productId)
                .map(favouriteProduct -> true)
                .defaultIfEmpty(false);
    }

    @ModelAttribute
    public Mono<CsrfToken> loadCsrfToken(ServerWebExchange exchange) {
        Mono<CsrfToken> attribute = exchange.getAttribute(CsrfToken.class.getName());
        if (attribute != null) {
            return attribute.doOnSuccess(token -> {
                exchange.getAttributes().put(CsrfRequestDataValueProcessor.DEFAULT_CSRF_ATTR_NAME, token);
            });
        }
        return Mono.empty();
    }

    @GetMapping
    public Mono<String> getProduct(Model model, @PathVariable int productId,
                                   @ModelAttribute("inFavourite") boolean inFavourite) {

        model.addAttribute("inFavourite", inFavourite);

        return productsReviewsWebClient.findProductReviewsByProduct(productId)
                .collectList()
                .doOnNext(productReviews -> model.addAttribute("reviews", productReviews))
                .thenReturn("customer/products/product");
    }

    @PostMapping("/add-favourite-product")
    public Mono<String> addFavouriteProduct(@ModelAttribute("product") Mono<Product> productMono) {
        return productMono
                .map(Product::id)
                .flatMap(productId -> favouriteProductsWebClient.addProductToFavourites(productId)
                        .thenReturn("redirect:/customer/products/%d".formatted(productId))
                        .onErrorResume(exception -> {
                            log.error(exception.getMessage(), exception);
                            return Mono.just("redirect:/customer/products/%d".formatted(productId));
                        }));
    }

    @PostMapping("/remove-favourite-product")
    public Mono<String> removeFavouriteProduct(@ModelAttribute("product") Mono<Product> productMono) {
        return productMono
                .map(Product::id)
                .flatMap(productId -> favouriteProductsWebClient.removeProductFromFavourites(productId)
                        .thenReturn("redirect:/customer/products/%d".formatted(productId)));
    }

    @PostMapping("/create-review")
    public Mono<String> createReview(@PathVariable int productId, NewProductReviewPayload payload,
                                     Model model,
                                     @ModelAttribute("inFavourite") boolean inFavourite) {
        return productsReviewsWebClient.createNewProductReview(productId, payload)
                .thenReturn("redirect:/customer/products/%d".formatted(productId))
                .onErrorResume(ClientBadRequestException.class, ex -> {
                    model.addAttribute("inFavourite", inFavourite);
                    model.addAttribute("payload", payload);
                    model.addAttribute("errors", ex.getErrors());
                    return Mono.just("customer/products/product");}
                );
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "customer/products/errors/404";
    }

}
