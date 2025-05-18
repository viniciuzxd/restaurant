package com.facol.restaurant.dto;

import com.facol.restaurant.entity.TagEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantRequestDTO {
    private String name;
    private String address;
    private TagEnum tag;
}
