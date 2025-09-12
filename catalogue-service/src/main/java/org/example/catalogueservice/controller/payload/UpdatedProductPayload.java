package org.example.catalogueservice.controller.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatedProductPayload(
        @NotBlank(message = "{catalogue.products.create.error.name_is_null}")
        @Size(min = 3, max = 50, message = "{catalogue.products.create.error.name_size_error}")
        String name,

        @NotBlank(message = "{catalogue.products.create.error.description_is_null}")
        @Size(max = 1000, message = "{catalogue.products.create.error.description_size_error}" )
        String description) {
}
