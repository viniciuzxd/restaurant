package com.facol.restaurant.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
public class ReviewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String reviewText;

    @OneToOne
    @JoinColumn(name = "author", referencedColumnName = "name")
    private UserEntity author;

    @OneToOne
    @JoinColumn(name = "restaurantId", referencedColumnName = "id")
    private RestaurantEntity restaurant;

    @OneToOne
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private UserEntity user;
}
