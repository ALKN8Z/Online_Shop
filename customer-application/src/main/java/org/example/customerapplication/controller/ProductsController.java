package org.example.customerapplication.controller;

import org.example.customerapplication.client.FavouriteProductsWebClient;
import org.example.customerapplication.client.ProductsWebClient;
import lombok.RequiredArgsConstructor;
import org.example.customerapplication.entity.FavouriteProduct;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@RequestMapping("customer/products")
public class ProductsController {
    private final ProductsWebClient productsClient;

    private final FavouriteProductsWebClient favouriteProductsWebClient;

    @GetMapping("/list")
    public Mono<String> getProducts(Model model,
                                       @RequestParam(name = "filter", required = false) String filter) {
        model.addAttribute("filter", filter);
        return productsClient.findAllProducts(filter)
                .collectList()
                .doOnNext(products -> model.addAttribute("products", products))
                .thenReturn("customer/products/list");

//        return productsClient.findAllProducts(filter)
//                .collectList()
//                .map(products -> Rendering.view("customer/products/list")
//                        .modelAttribute("filter", filter)
//                        .modelAttribute("products", products)
//                        .build());
    }


    @GetMapping("/favourites")
    public Mono<String> getFavouriteProducts(Model model,
                                             @RequestParam(name = "filter", required = false) String filter) {
        model.addAttribute("filter", filter);
        return favouriteProductsWebClient.getAllFavouriteProducts()
                .map(FavouriteProduct::getProductId)
                .collectList()
                .flatMap(favouriteProducts -> productsClient.findAllProducts(filter)
                        .filter(product -> favouriteProducts.contains(product.id()))
                        .collectList()
                        .doOnNext(products -> model.addAttribute("products", products)))
                .thenReturn("customer/products/favourites");
    }

}
