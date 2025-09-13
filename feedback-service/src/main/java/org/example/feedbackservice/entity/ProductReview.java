package org.example.feedbackservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductReview {
    @Id
    private UUID id;
    private int productId;
    private Integer rating;
    private String content;
}
