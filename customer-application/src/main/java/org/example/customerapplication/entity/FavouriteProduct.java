package org.example.customerapplication.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

public record FavouriteProduct (UUID id, int productId) {}
