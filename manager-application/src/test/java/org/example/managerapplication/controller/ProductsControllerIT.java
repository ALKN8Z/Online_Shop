package org.example.managerapplication.controller;


import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.example.managerapplication.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@WireMockTest(httpPort = 54321)
public class ProductsControllerIT {

    @Autowired
    MockMvc mockMvc;


    @Test
    @WithMockUser(roles = "MANAGER")
    public void getNewProductPage_shouldReturnProductPage() throws Exception {
        mockMvc.perform(get("/catalogue/products/create"))
                .andExpectAll(status().isOk(),
                        view().name("catalogue/products/create")
                );
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void getProductList_shouldReturnProductList() throws Exception {
        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/catalogue-api/products"))
                .withQueryParam("filter", WireMock.equalTo("name"))
                .willReturn(WireMock.ok("""
                        [
                        {
                            "id": 1,
                            "name": "name1",
                            "description": "description1"
                        },
                        {
                            "id": 2,
                            "name": "name2",
                            "description": "description2"
                        }
                        ]
                        """).withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        mockMvc.perform(get("/catalogue/products/list")
                        .queryParam("filter", "name"))
                .andExpectAll(
                        status().isOk(),
                        view().name("catalogue/products/list"),
                        model().attribute("filter", "name"),
                        model().attribute("products", List.of(
                                new Product(1, "name1", "description1"),
                                new Product(2, "name2", "description2"))));

        WireMock.verify(WireMock.getRequestedFor(
                WireMock.urlPathMatching("/catalogue-api/products"))
                .withQueryParam("filter", WireMock.equalTo("name")));
    }

}
