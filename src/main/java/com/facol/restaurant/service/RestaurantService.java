package com.facol.restaurant.service;

import com.facol.restaurant.dto.RestaurantResponseDTO;
import com.facol.restaurant.entity.RestaurantEntity;
import com.facol.restaurant.exception.NotFoundException;
import com.facol.restaurant.repository.RestaurantRepositoy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantService {
    RestaurantRepositoy restaurantRepositoy;

    public RestaurantService(RestaurantRepositoy restaurantRepositoy) {
        this.restaurantRepositoy = restaurantRepositoy;
    }

    public RestaurantResponseDTO getRestaurantById(long id) {
        return restaurantRepositoy.findById(id)
                .map(u -> new RestaurantResponseDTO(
                        u.getId(),
                        u.getName(),
                        u.getAddress(),
                        u.getTag().toString())
                )
                .orElseThrow(() -> new NotFoundException("Restaurante não encontrado"));
    }

    public Page<RestaurantResponseDTO> getRestaurants() {
        return findAll();
    }


    /*
        Retorna todos os restaurantes paginados
    */
    public Page<RestaurantResponseDTO> findAll() {
        int page = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(
                page,
                size,
                Sort.Direction.ASC,
                "tag");

        Page<RestaurantEntity> restaurantList = restaurantRepositoy.findAll(pageRequest);

        /*
        GetContent: retorna a lista interna de restaurantList(page)
        stream: converte a lista em stream, é uma forma funcional de processar dados em sequência
        map: para cada elemento da lista(restaurant), transforma em um novo objeto RestaurantResponse
        collect: transformar todos os elementos do stream, você coleta tudo de volta em uma Lista
        */
        List<RestaurantResponseDTO> dtoList = restaurantList
                .getContent()
                .stream()
                .map(restaurant -> new RestaurantResponseDTO(
                        restaurant.getId(),
                        restaurant.getName(),
                        restaurant.getAddress(),
                        restaurant.getTag().toString())
                )
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageRequest, restaurantList.getTotalElements());
    }
}
