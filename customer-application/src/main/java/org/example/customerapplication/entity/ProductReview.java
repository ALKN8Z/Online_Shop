package org.example.customerapplication.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductReview(
        UUID id,
        int productId,
        int rating,
        String content,
        String ownerName,
        LocalDateTime createdAt) {
}
