package com.facol.restaurant.service;

import com.facol.restaurant.dto.ReviewCreateDto;
import com.facol.restaurant.dto.ReviewRequestDto;
import com.facol.restaurant.dto.ReviewResponseDto;
import com.facol.restaurant.entity.ReviewEntity;
import com.facol.restaurant.entity.Enum.RestaurantEnum;
import com.facol.restaurant.exception.NotFoundException;
import com.facol.restaurant.repository.RestaurantRepositoy;
import com.facol.restaurant.repository.ReviewRepository;
import com.facol.restaurant.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final RestaurantRepositoy restaurantRepositoy;

    public ReviewService(ReviewRepository reviewRepository, RestaurantRepositoy restaurantRepositoy, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.restaurantRepositoy = restaurantRepositoy;
        this.userRepository = userRepository;
    }

    public ReviewResponseDto getReviewById(Long id) {
        ReviewEntity reviewEntity = reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Review não encontrada"));

        String authorName = (reviewEntity.getAuthor() != null)
                ? reviewEntity.getAuthor().getName()
                : "Autor desconhecido";

        return new ReviewResponseDto(
                reviewEntity.getId(),
                reviewEntity.getUser() != null ? reviewEntity.getUser().getId(): null,
                reviewEntity.getRestaurant() != null ? reviewEntity.getRestaurant().getName() : "Restaurante desconhecido",
                authorName,
                reviewEntity.getReviewText(),
                reviewEntity.getRating()
        );
    }



    public Page<ReviewResponseDto> getReviews() {
        return findAll();
    }


    /*
        retornar todas as reviews de um restaurante em especifico paginadas, atraves do id.
    */
    public Page<ReviewResponseDto> getReviewsByRestaurantId(Long restaurantId) {
        restaurantRepositoy.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurante não encontrado"));

        int page = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC, "id");

        Page<ReviewEntity> reviews = reviewRepository.findAll(pageRequest);

        List<ReviewResponseDto> dtoList = reviews.stream()
                .map(review -> new ReviewResponseDto(
                        review.getId(),
                        review.getUser().getId(),
                        review.getRestaurant().getName(),
                        review.getAuthor().getName(),
                        review.getReviewText(),
                        review.getRating()
                ))
                .collect(Collectors.toList());


        return new PageImpl<>(dtoList, pageRequest, dtoList.size());
    }

    /*
        cria a review, coloca a tag e adiciona a lista das entidades.
    */
    public void createReview(ReviewCreateDto reviewCreate, Long userId, Long restaurantId) {
        var userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Id do usuário não encontrado"));
        var restaurantEntity = restaurantRepositoy.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurante não encontrado"));

        ReviewEntity reviewEntity = new ReviewEntity();

        reviewEntity.setUser(userEntity);
        reviewEntity.setAuthor(userEntity);
        reviewEntity.setRestaurant(restaurantEntity);
        reviewEntity.setReviewText(reviewCreate.getReviewText());
        reviewEntity.setRating(reviewCreate.getRating());
        setTag(restaurantId, reviewCreate.getRating());

        restaurantEntity.getReviews().add(reviewEntity);
        userEntity.getReviews().add(reviewEntity);

        reviewRepository.save(reviewEntity);
    }

    public void updateReview(Long id,ReviewRequestDto reviewUpdate) {
        var reviewEntity =  reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Review não encontrada"));
        var userEntity = userRepository.findById(reviewUpdate.getUserid())
                .orElseThrow(() -> new NotFoundException("Id do usuário não encontrado"));
        var restaurantEntity = restaurantRepositoy.findById(reviewUpdate.getRestaurantId())
                .orElseThrow(() -> new NotFoundException("Restaurante não encontrado"));

        reviewEntity.setUser(userEntity);
        reviewEntity.setAuthor(userEntity);
        reviewEntity.setRestaurant(restaurantEntity);
        reviewEntity.setReviewText(reviewUpdate.getReviewText());
        reviewEntity.setRating(reviewUpdate.getRating());
        setTag(reviewUpdate.getRestaurantId(), reviewUpdate.getRating());

        reviewRepository.save(reviewEntity);
    }

    public void pathReview(Long id, ReviewRequestDto parcialUpdate) {
        var reviewEntity =  reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Review não encontrada"));
        userRepository.findById(parcialUpdate.getUserid())
                .orElseThrow(() -> new NotFoundException("Id do usuário não encontrado"));
        restaurantRepositoy.findById(parcialUpdate.getRestaurantId())
                .orElseThrow(() -> new NotFoundException("Restaurante não encontrado"));

        if (parcialUpdate.getReviewText() != null) {
            reviewEntity.setReviewText(parcialUpdate.getReviewText());
        }
        if (parcialUpdate.getRating() != null){
            reviewEntity.setRating(parcialUpdate.getRating());
            setTag(parcialUpdate.getRestaurantId(), parcialUpdate.getRating());
        }
        if (parcialUpdate.getUserid() != null) {
            reviewEntity.getUser().setId(parcialUpdate.getUserid());
        }
        if (parcialUpdate.getRestaurantId() != null) {
            reviewEntity.getRestaurant().setId(parcialUpdate.getRestaurantId());
        }
        reviewRepository.save(reviewEntity);
    }

    public void deleteReview(Long id) {
        var reviewEntity =  reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Review não encontrada"));
        reviewRepository.delete(reviewEntity);
    }
    /*
        Retorna todos as reviews paginados.
    */
    public Page<ReviewResponseDto> findAll() {
        int page = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(
                page,
                size,
                Sort.Direction.ASC,
                "id");

        Page<ReviewEntity> reviewList = reviewRepository.findAll(pageRequest);

        /*
        GetContent: retorna a lista interna de reviewlist(page)
        stream: converte a lista em stream, é uma forma funcional de processar dados em sequência
        map: para cada elemento da lista(user), transforma em um novo objeto UserResponse
        collect: transformar todos os elementos do stream, você coleta tudo de volta em uma Lista
        */
        List<ReviewResponseDto> dtoList = reviewList
                .getContent()
                .stream()
                .map(review -> new ReviewResponseDto(
                        review.getId(),
                        review.getUser().getId(),
                        review.getRestaurant().getName(),
                        review.getUser().getName(),
                        review.getReviewText(),
                        review.getRating()
                        )
                )
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageRequest, reviewList.getTotalElements());
    }


    /*
        calcular a media de um restaurante.
    */
    public double media (Long restaurantId, double rating) {
        List<ReviewEntity> reviewList = reviewRepository.findAllByRestaurantId(restaurantId);
        double sum = rating;
        for (ReviewEntity review : reviewList) {
            sum += review.getRating();
        }
        return sum / reviewList.size();
    }

    /*
        definir a tag do restaurante apartir de sua media.
    */
    public void setTag(Long restaurantId, double rating) {
        double num = media(restaurantId, rating);
        if(num >= 8) {
            var restaurantEntity = restaurantRepositoy.findById(restaurantId)
                    .orElseThrow(() -> new RuntimeException("Restaurante não encontrado"));
            restaurantEntity.setTag(RestaurantEnum.MUITO_BOM);
        }
        if(num >= 4 && num <= 7) {
            var restaurantEntity = restaurantRepositoy.findById(restaurantId)
                    .orElseThrow(() -> new NotFoundException("Restaurante não encontrado"));
            restaurantEntity.setTag(RestaurantEnum.BOM);
        }
        if (num >= 0 && num <= 3) {
            var restaurantEntity = restaurantRepositoy.findById(restaurantId)
                    .orElseThrow(() -> new NotFoundException("Restaurante não encontrado"));
            restaurantEntity.setTag(RestaurantEnum.RUIM);
        }
    }
}
