package com.facol.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponseDto {
    private long id;
    private long userId;
    private String restaurantName;
    private String author;
    private String reviewText;
    private double rating;
}
