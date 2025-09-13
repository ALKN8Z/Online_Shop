package org.example.feedbackservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductReview {
    private UUID id;
    private int productId;
    private Integer rating;
    private String content;
}
