package com.facol.restaurant.service;

import com.facol.restaurant.dto.RestaurantRequestDto;
import com.facol.restaurant.dto.RestaurantResponseDto;
import com.facol.restaurant.entity.RestaurantEntity;
import com.facol.restaurant.entity.Enum.RestaurantEnum;
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

    public RestaurantResponseDto getRestaurantById(long id) {
        return restaurantRepositoy.findById(id)
                .map(u -> new RestaurantResponseDto(
                        u.getId(),
                        u.getName(),
                        u.getAddress(),
                        u.getTag().toString())
                )
                .orElseThrow(() -> new NotFoundException("Restaurante não encontrado"));
    }

    public Page<RestaurantResponseDto> getRestaurants() {
        return findAll();
    }


    /*
        Retorna todos os restaurantes paginados
    */
    public Page<RestaurantResponseDto> findAll() {
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
        List<RestaurantResponseDto> dtoList = restaurantList
                .getContent()
                .stream()
                .map(restaurant -> new RestaurantResponseDto(
                        restaurant.getId(),
                        restaurant.getName(),
                        restaurant.getAddress(),
                        restaurant.getTag().toString())
                )
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageRequest, restaurantList.getTotalElements());
    }

    public void createRestaurant(RestaurantRequestDto restaurantCreate) {
        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setName(restaurantCreate.getName());
        restaurantEntity.setAddress(restaurantCreate.getAddress());
        restaurantEntity.setTag(RestaurantEnum.INDEFINIDO);

        restaurantRepositoy.save(restaurantEntity);
    }

    public void updateRestaurant(Long id, RestaurantRequestDto restaurantUpdate) {
        RestaurantEntity restaurant = restaurantRepositoy.findById(id)
                .orElseThrow(() -> new NotFoundException("Restaurante não encontrado"));
        restaurant.setName(restaurantUpdate.getName());
        restaurant.setAddress(restaurantUpdate.getAddress());
        restaurantRepositoy.save(restaurant);
    }

    public void patchRestaurant(Long id, RestaurantRequestDto parcialUpdate) {
        RestaurantEntity restaurant = restaurantRepositoy.findById(id)
                .orElseThrow(() -> new NotFoundException("Restaurante não encontrado"));

        if (parcialUpdate.getName() != null) {
            restaurant.setName(parcialUpdate.getName());
        }
        if (parcialUpdate.getAddress() != null) {
            restaurant.setAddress(parcialUpdate.getAddress());
        }
        restaurantRepositoy.save(restaurant);
    }

    public void delete(Long id) {
        RestaurantEntity restaurant = restaurantRepositoy.findById(id)
                .orElseThrow(() -> new NotFoundException("Restaurante não encontrado"));
        restaurantRepositoy.delete(restaurant);
    }
}
