package org.example.managerapplication.controller;


import org.example.managerapplication.client.ProductsRestClient;
import org.example.managerapplication.controller.payload.NewProductPayload;
import org.example.managerapplication.exception.BadRequestException;
import org.example.managerapplication.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тест для ProductsController")
public class ProductsControllerTest {
    @Mock
    ProductsRestClient productsRestClient;

    @InjectMocks
    ProductsController productsController;


    @Test
    @DisplayName("createProduct создать новый товар и перенаправит на страницу с новым товаром")
    public void createProduct_shouldReturnRedirectToProductPage() {
        NewProductPayload payload = new NewProductPayload("Название продукта", "Описание продукта");
        Model model = new ConcurrentModel();
        when(productsRestClient.createProduct("Название продукта", "Описание продукта"))
                .thenReturn(new Product(1, "Название продукта", "Описание продукта"));

        String result = productsController.createProduct(payload, model);

        assertThat(result).isEqualTo("redirect:/catalogue/products/1");
        verify(productsRestClient).createProduct("Название продукта", "Описание продукта");
        verifyNoMoreInteractions(productsRestClient);

    }

    @Test
    @DisplayName("createProduct должен вернуть страницу создания товара с ошибками")
    public void createProduct_shouldReturnCreateProductPageWithErrors() {
        NewProductPayload payload = new NewProductPayload("", null);
        Model model = new ConcurrentModel();
        when(productsRestClient.createProduct(any(), any())).thenThrow(new BadRequestException(List.of("Error1", "Error2")));

        String result = productsController.createProduct(payload, model);

        assertThat(result).isEqualTo("catalogue/products/create");
        assertThat(model.getAttribute("payload")).isEqualTo(payload);
        assertThat(model.getAttribute("errors")).isEqualTo(List.of("Error1", "Error2"));
        verify(productsRestClient).createProduct(any(), any());
        verifyNoMoreInteractions(productsRestClient);

    }

    @Test
    @DisplayName("createProduct должен вернуть страницу для создания товара")
    public void createProduct_shouldReturnCreateProductPage() {


        String result = productsController.createProduct();

        assertThat(result).isEqualTo("catalogue/products/create");
    }

    @Test
    @DisplayName("getProducts должен вернуть страницу со списком всех товаров")
    public void getProducts_shouldReturnListOfProducts() {
        Model model = new ConcurrentModel();
        List<Product> products = List.of(
                new Product(1, "name1", "description1"),
                new Product(2, "name2", "description2"));
        when(productsRestClient.getProducts("фильтр")).thenReturn(products);

        String result = productsController.getProducts(model, "фильтр");

        assertThat(result).isEqualTo("catalogue/products/list");
        assertThat(model.getAttribute("products")).isEqualTo(products);
        assertThat(model.getAttribute("filter")).isEqualTo("фильтр");
        verify(productsRestClient).getProducts("фильтр");
        verifyNoMoreInteractions(productsRestClient);
    }


}
