package com.facol.restaurant.repository;

import com.facol.restaurant.entity.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepositoy extends JpaRepository<RestaurantEntity, Long> {
}
