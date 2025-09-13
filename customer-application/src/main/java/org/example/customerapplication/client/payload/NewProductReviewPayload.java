package org.example.customerapplication.client.payload;

public record NewProductReviewPayload(Integer productId, Integer rating, String content) {
}
