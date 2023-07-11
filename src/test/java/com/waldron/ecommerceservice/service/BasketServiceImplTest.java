package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.Basket;
import com.waldron.ecommerceservice.entity.BasketItem;
import com.waldron.ecommerceservice.exception.NotFoundException;
import com.waldron.ecommerceservice.repository.BasketItemRepository;
import com.waldron.ecommerceservice.repository.BasketRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class BasketServiceImplTest {

    @Mock
    private BasketRepository basketRepository;

    @Mock
    private BasketItemService basketItemService;

    @InjectMocks
    private BasketServiceImpl basketService;

    //todo fix test
    @Test
    public void getBasketForId_shouldReturnBasket_whenBasketIsFound(){

        Long basketId = 1l;
        Basket expectedBasket = Basket.builder()
                .id(basketId)
                .build();

        when(basketRepository.findById(basketId)).thenReturn(Mono.just(expectedBasket));

        StepVerifier.create(basketService.getBasketForId(basketId))
                .expectNext(expectedBasket)
                .verifyComplete();
    }

    //todo fix test
    @Test
    public void getBasketForId_shouldReturnError_whenBasketNotFound(){

        Long basketId = 1l;

        when(basketRepository.findById(basketId)).thenReturn(Mono.empty());

        StepVerifier.create(basketService.getBasketForId(basketId))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    public void getBasketForId_shouldPopulateBasketItems_whenBasketItemsArePresent(){

        Long basketId = 1l;
        Long basketItem1Id = 1l;
        Long basketItem2Id = 2l;
        Basket repositoryBasket = Basket.builder()
                .id(basketId)
                .build();

        BasketItem basketItem1 = BasketItem.builder()
                .id(basketItem1Id)
                .basketId(basketId)
                .productCount(1)
                .build();

        BasketItem basketItem2 = BasketItem.builder()
                .id(basketItem1Id)
                .basketId(basketItem2Id)
                .productCount(1)
                .build();

        Set<BasketItem> basketItemSet = new HashSet<>();
        basketItemSet.add(basketItem1);
        basketItemSet.add(basketItem2);

        Basket expectedBasket = Basket.builder()
                .id(basketId)
                .basketItems(basketItemSet)
                .build();

        when(basketRepository.findById(basketId)).thenReturn(Mono.just(repositoryBasket));
        when(basketItemService.getBasketItemsForBasketId(basketId)).thenReturn(Flux.just(basketItem1, basketItem2));

        StepVerifier.create(basketService.getBasketForId(basketId))
                .expectNext(expectedBasket)
                .verifyComplete();

    }

}