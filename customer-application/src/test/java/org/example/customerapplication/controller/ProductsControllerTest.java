package org.example.customerapplication.controller;

import org.example.customerapplication.client.FavouriteProductsWebClient;
import org.example.customerapplication.client.ProductsWebClient;
import org.example.customerapplication.entity.FavouriteProduct;
import org.example.customerapplication.entity.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductsControllerTest {

    @Mock
    ProductsWebClient productsClient;

    @Mock
    FavouriteProductsWebClient favouriteProductsWebClient;

    @InjectMocks
    ProductsController productsController;



    @Test
    void getProducts_ReturnsProductsListPage(){
        Model model = new ConcurrentModel();

        doReturn(Flux.fromIterable(List.of(
                new Product(1, "product 1", "description 1"),
                new Product(2, "product 2", "description 2"),
                new Product(3, "product 3", "description 3")
        ))).when(productsClient).findAllProducts("filter");


        StepVerifier.create(productsController.getProducts(model, "filter"))
                .expectNext("customer/products/list")
                .verifyComplete();

        assertEquals(List.of(
                new Product(1, "product 1", "description 1"),
                new Product(2, "product 2", "description 2"),
                new Product(3, "product 3", "description 3")
        ), model.getAttribute("products"));
        assertEquals("filter", model.getAttribute("filter"));

        verify(productsClient).findAllProducts("filter");
        verifyNoMoreInteractions(productsClient);
        verifyNoInteractions(favouriteProductsWebClient);

    }

    @Test
    void getFavouriteProducts_ReturnsFavouriteProductsListPage(){
        Model model = new ConcurrentModel();

        doReturn(Flux.fromIterable(List.of(
                new FavouriteProduct(UUID.fromString("d0d39c61-3011-47fd-a16a-75c6d517a11f"), 1),
                new FavouriteProduct(UUID.fromString("9d3e65ea-2b69-4eb9-8524-4dc0d00d059a"), 2)
        ))).when(favouriteProductsWebClient).getAllFavouriteProducts();
        doReturn(Flux.fromIterable(List.of(
                new Product(1, "product 1", "description 1"),
                new Product(2, "product 2", "description 2")
        ))).when(productsClient).findAllProducts("filter");


        StepVerifier.create(productsController.getFavouriteProducts(model, "filter"))
                .expectNext("customer/products/favourites")
                .verifyComplete();

        assertEquals("filter", model.getAttribute("filter"));
        assertEquals(List.of(
                new Product(1, "product 1", "description 1"),
                new Product(2, "product 2", "description 2")
        ), model.getAttribute("products"));

        verify(productsClient).findAllProducts("filter");
        verify(favouriteProductsWebClient).getAllFavouriteProducts();
        verifyNoMoreInteractions(favouriteProductsWebClient, productsClient);

    }

}