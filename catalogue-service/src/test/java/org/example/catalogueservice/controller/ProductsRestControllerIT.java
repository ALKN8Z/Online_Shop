package org.example.catalogueservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@Transactional
public class ProductsRestControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql("/sql/test-products.sql")
    @WithMockUser(authorities = "SCOPE_view_catalogue")
    public void getProducts_shouldReturnAllProductsByFilter() throws Exception {

        mockMvc.perform(get("/catalogue-api/products")
                .param("filter", "тов")).andExpectAll(
                status().isOk(),
                content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                content().json("""
                            [
                            {"id": 1, "name":"Товар 1", "description":"Описание 1"},
                            {"id": 4, "name":"Товар 4", "description":"Описание 4"}
                            ]
                            """)
        );

    }

    @Test
    @WithMockUser(authorities = "SCOPE_edit_catalogue")
    public void createProduct_shouldReturnProduct() throws Exception {
        mockMvc.perform(post("/catalogue-api/products/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name": "Товар", "description": "Описание"}
                        """))
                .andExpectAll(
                        status().isCreated(),
                        header().string(HttpHeaders.LOCATION, "http://localhost/catalogue-api/products/1"),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                        {"id": 1, "name": "Товар", "description": "Описание"}
                                        """));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_edit_catalogue")
    public void createProduct_shouldReturnBadRequestIfRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/catalogue-api/products/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                            "name": null,
                            "description": null
                            }
                            """))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                        {
                                            "errors": [
                                                "Название товара не может быть пустым",
                                                "Описание товара не может быть пустым"
                                            ]
                                        }
                                        """));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_view_catalogue")
    public void createProduct_shouldReturnForbiddenIfUserIsUnauthorized() throws Exception {
        mockMvc.perform(post("/catalogue-api/products/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "name": null,
                        "description": null
                        }"""))
                .andExpect(status().isForbidden());
    }
}
