package org.example.customerapplication.controller;

import org.example.customerapplication.client.FavouriteProductsWebClient;
import org.example.customerapplication.client.ProductsReviewsWebClient;
import org.example.customerapplication.client.ProductsWebClient;
import org.example.customerapplication.client.exception.ClientBadRequestException;
import org.example.customerapplication.controller.payload.NewProductReviewPayload;
import org.example.customerapplication.entity.FavouriteProduct;
import org.example.customerapplication.entity.Product;
import org.example.customerapplication.entity.ProductReview;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.security.core.parameters.P;
import org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    ProductsWebClient productsWebClient;

    @Mock
    FavouriteProductsWebClient favouriteProductsWebClient;

    @Mock
    ProductsReviewsWebClient productsReviewsWebClient;

    @InjectMocks
    ProductController productController;

    @Test
    void loadProduct_ReturnsProduct() {
        Product product = new Product(1, "product 1", "description 1");

        doReturn(Mono.just(product)).when(productsWebClient).findProduct(1);

        StepVerifier.create(productController.loadProduct(1))
                .expectNext(product)
                .verifyComplete();

        verify(productsWebClient).findProduct(1);
        verifyNoMoreInteractions(productsWebClient);
        verifyNoInteractions(favouriteProductsWebClient, productsReviewsWebClient);
    }

    @Test
    void loadProduct_IfProductDoesNotExist_ThrowsNoSuchElementException() {

        doReturn(Mono.empty()).when(productsWebClient).findProduct(1);

        StepVerifier.create(productController.loadProduct(1))
                .expectErrorMatches(exception -> exception instanceof NoSuchElementException
                        && exception.getMessage().equals("customer.products.error.not_found"))
                .verify();

        verify(productsWebClient).findProduct(1);
        verifyNoMoreInteractions(productsWebClient);
        verifyNoInteractions(favouriteProductsWebClient, productsReviewsWebClient);
    }

    @Test
    void inFavouriteProduct_ReturnsTrue(){
        FavouriteProduct product = new FavouriteProduct(UUID.fromString("d0d39c61-3011-47fd-a16a-75c6d517a11f"), 1);

        doReturn(Mono.just(product)).when(favouriteProductsWebClient).getFavouriteProduct(1);

        StepVerifier.create(productController.inFavouriteProduct(1))
                .expectNext( true)
                .verifyComplete();

        verify(favouriteProductsWebClient).getFavouriteProduct(1);
        verifyNoMoreInteractions(favouriteProductsWebClient);
        verifyNoInteractions(productsReviewsWebClient, productsWebClient);
    }

    @Test
    void inFavouriteProduct_IfProductNotInFavourites_ReturnsFalse(){
        doReturn(Mono.empty()).when(favouriteProductsWebClient).getFavouriteProduct(1);

        StepVerifier.create(productController.inFavouriteProduct(1))
                .expectNext( false)
                .verifyComplete();

        verify(favouriteProductsWebClient).getFavouriteProduct(1);
        verifyNoMoreInteractions(favouriteProductsWebClient);
        verifyNoInteractions(productsReviewsWebClient, productsWebClient);
    }


    @Test
    void loadCsrfToken_ReturnsCsrfTokenAndPutInExchangeAttributes() {
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        CsrfToken csrfToken = mock(CsrfToken.class);
        Map<String, Object> attributes = new HashMap<>();

        doReturn(Mono.just(csrfToken))
                .when(exchange).getAttribute(CsrfToken.class.getName());
        doReturn(attributes)
                .when(exchange).getAttributes();

        StepVerifier.create(productController.loadCsrfToken(exchange))
                .expectNext(csrfToken)
                .verifyComplete();

        assertEquals(attributes.get(CsrfRequestDataValueProcessor.DEFAULT_CSRF_ATTR_NAME), csrfToken);
        verify(exchange).getAttribute(CsrfToken.class.getName());
        verify(exchange).getAttributes();
        verifyNoMoreInteractions(exchange);

    }

    @Test
    void loadCsrf_IfCsrfTokenIsNull_ReturnsEmptyMono() {
        ServerWebExchange exchange = mock(ServerWebExchange.class);

        doReturn(null)
                .when(exchange).getAttribute(CsrfToken.class.getName());

        StepVerifier.create(productController.loadCsrfToken(exchange))
                .verifyComplete();

        verify(exchange).getAttribute(CsrfToken.class.getName());
        verifyNoMoreInteractions(exchange);
    }

    @Test
    void getProduct_ReturnsProductPage(){
        Model model = new ConcurrentModel();

        doReturn(Flux.fromIterable(List.of(
                new ProductReview(
                        UUID.fromString("d0d39c61-3011-47fd-a16a-75c6d517a11f"),
                        2,
                        1,
                        "good",
                        "test",
                        LocalDateTime.of(2005, 3, 2, 1, 10))
        ))).when(productsReviewsWebClient).findProductReviewsByProduct(1);

        StepVerifier.create(productController.getProduct(model, 1, true))
                .expectNext("customer/products/product")
                .verifyComplete();

        assertEquals(
                List.of(new ProductReview(
                        UUID.fromString("d0d39c61-3011-47fd-a16a-75c6d517a11f"),
                        2,
                        1,
                        "good",
                        "test",
                        LocalDateTime.of(2005, 3, 2, 1, 10))),
                model.getAttribute("reviews"));
        assertEquals(true, model.getAttribute("inFavourite"));

        verify(productsReviewsWebClient).findProductReviewsByProduct(1);
        verifyNoMoreInteractions(productsReviewsWebClient);

    }


    @Test
    void addFavouriteProduct_ReturnsProductPage(){
        Product product = new Product(1, "product", "description");

        doReturn(Mono.just(product))
                .when(favouriteProductsWebClient).addProductToFavourites(1);

        StepVerifier.create(productController.addFavouriteProduct(Mono.just(product)))
                .expectNext("redirect:/customer/products/1")
                .verifyComplete();

        verify(favouriteProductsWebClient).addProductToFavourites(1);
        verifyNoMoreInteractions(favouriteProductsWebClient);
    }

    @Test
    void addFavouriteProduct_IfExceptionThrown_ReturnsProductPageWithoutAddingToFavourites(){
        Product product = new Product(1, "product", "description");

        doReturn(Mono.error(new ClientBadRequestException(new RuntimeException(), List.of("Некорректный запрос"))))
                .when(favouriteProductsWebClient).addProductToFavourites(1);

        StepVerifier.create(productController.addFavouriteProduct(Mono.just(product)))
                .expectNext("redirect:/customer/products/1")
                .verifyComplete();

        verify(favouriteProductsWebClient).addProductToFavourites(1);
        verifyNoMoreInteractions(favouriteProductsWebClient);
    }

    @Test
    void removeFavouriteProduct_ReturnsProductPage(){
        Product product = new Product(1, "product", "description");

        doReturn(Mono.just(product))
                .when(favouriteProductsWebClient).removeProductFromFavourites(1);

        StepVerifier.create(productController.removeFavouriteProduct(Mono.just(product)))
                .expectNext("redirect:/customer/products/1")
                .verifyComplete();

        verify(favouriteProductsWebClient).removeProductFromFavourites(1);
        verifyNoMoreInteractions(favouriteProductsWebClient);
        verifyNoInteractions(productsReviewsWebClient, productsWebClient);
    }

    @Test
    void createReview_RedirectToProductPage(){
        NewProductReviewPayload payload = new NewProductReviewPayload(1, "test-content");
        Model model = new ConcurrentModel();
        MockServerHttpResponse response = new MockServerHttpResponse();

        doReturn(Mono.just(new ProductReview(
                UUID.fromString("d0d39c61-3011-47fd-a16a-75c6d517a11f"),
                1,
                1,
                "test-content",
                "test-owner",
                LocalDateTime.of(2000, 1, 2, 3, 1)
        ))).when(productsReviewsWebClient).createNewProductReview(1, payload);

        StepVerifier.create(productController.createReview(1, payload, model, true))
                .expectNext("redirect:/customer/products/1")
                .verifyComplete();

        assertNull(response.getStatusCode());

        verify(productsReviewsWebClient).createNewProductReview(1, payload);
        verifyNoMoreInteractions(productsReviewsWebClient);
        verifyNoInteractions(productsWebClient, favouriteProductsWebClient);
    }

    @Test
    void createReview_IfRequestIsInvalid_ReturnsProductPageWithPayloadAndErrors(){
        NewProductReviewPayload payload = new NewProductReviewPayload(1, "test-content");
        Model model = new ConcurrentModel();

        doReturn(Mono.error(new ClientBadRequestException(new RuntimeException(), List.of("Something went wrong"))))
                .when(productsReviewsWebClient).createNewProductReview(1, payload);

        StepVerifier.create(productController.createReview(1, payload, model, true))
                .expectNext("customer/products/product")
                .verifyComplete();

        assertEquals(true, model.getAttribute("inFavourite"));
        assertEquals(new NewProductReviewPayload(1, "test-content"), model.getAttribute("payload"));
        assertEquals(List.of("Something went wrong"), model.getAttribute("errors"));

        verify(productsReviewsWebClient).createNewProductReview(1, payload);
        verifyNoMoreInteractions(productsReviewsWebClient);
        verifyNoInteractions(productsWebClient, favouriteProductsWebClient);

    }

    @Test
    void handleNoSuchException_ReturnErrorPage(){
        Model model = new ConcurrentModel();
        ServerHttpResponse response = new MockServerHttpResponse();

        String result = productController
                .handleNoSuchElementException(new NoSuchElementException("Товар не найден"), model, response);

        assertEquals("customer/products/errors/404", result);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Товар не найден", model.getAttribute("error"));
    }


}