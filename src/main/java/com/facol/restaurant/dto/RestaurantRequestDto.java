package com.facol.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantRequestDto {
    @NotBlank(message = "Nome obrigatorio")
    private String name;
    @NotBlank(message = "Endereço obrigatorio")
    private String address;
}
