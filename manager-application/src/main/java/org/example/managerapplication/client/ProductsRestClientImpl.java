package org.example.managerapplication.client;

import lombok.RequiredArgsConstructor;
import org.example.managerapplication.controller.payload.NewProductPayload;
import org.example.managerapplication.controller.payload.UpdatedProductPayload;
import org.example.managerapplication.exception.BadRequestException;
import org.example.managerapplication.model.Product;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
public class ProductsRestClientImpl implements ProductsRestClient {

    private final RestClient restClient;

    private static final ParameterizedTypeReference<List<Product>> productTypeReference =
            new ParameterizedTypeReference<>() {
            };

    @Override
    public List<Product> getProducts(String filter) {
        return restClient
                .get()
                .uri("catalogue-api/products?filter={filter}", filter)
                .retrieve()
                .body(productTypeReference);

    }

    @Override
    public Optional<Product> getProduct(int productId) {

        try {
            return Optional.ofNullable(restClient
                    .get()
                    .uri("catalogue-api/products/{productId}", Map.of("productId", productId))
                    .retrieve()
                    .body(Product.class));
        } catch (HttpClientErrorException.NotFound exception)
        {
            return Optional.empty();
        }

    }
    @Override
    public void updateProduct(int productId, String name, String description) {
        try{
            restClient
                    .patch()
                    .uri("catalogue-api/products/{productId}", Map.of("productId", productId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new UpdatedProductPayload(name, description))
                    .retrieve()
                    .toBodilessEntity();

        }
        catch (HttpClientErrorException.BadRequest exception){
            ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
            throw new BadRequestException((List<String>) problemDetail.getProperties().get("errors"));
        }
    }

    @Override
    public Product createProduct(String name, String description) {
        try {
            return restClient
                    .post()
                    .uri("catalogue-api/products/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new NewProductPayload(name, description))
                    .retrieve()
                    .body(Product.class);
        } catch (HttpClientErrorException.BadRequest exception){
            ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
            throw new BadRequestException((List<String>) problemDetail.getProperties().get("errors"));
        }
    }

    @Override
    public void deleteProduct(Product product) {
        try {
            restClient
                    .delete()
                    .uri("catalogue-api/products/{productId}", Map.of("productId", product.id()))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound exception){
            throw new NoSuchElementException(exception);
        }
    }
}
