package org.example.customerapplication.entity.payload;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NewProductReviewPayload(

        @NotNull(message = "{customer.products.reviews.create.errors.rating_is_null}")
        @Max(value = 5, message = "{customer.products.reviews.create.errors.rating_is_above_max}")
        @Min(value = 1, message = "{customer.products.reviews.create.errors.rating_is_below_min}")
        Integer rating,

        @Size(max = 1000, message = "{customer.products.reviews.create.errors.content_size_is_too_big}")
        String content) {}

