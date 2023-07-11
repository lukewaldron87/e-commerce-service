package com.waldron.ecommerceservice.repository;

import com.waldron.ecommerceservice.entity.Basket;
import com.waldron.ecommerceservice.entity.BasketItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
class BasketItemRepositoryTest {

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private BasketItemRepository basketItemRepository;

    @Test
    public void findByBasketId_shouldReturnAllBasketItemsWithGivenBasketId(){

        Basket basket1 = Basket.builder().build();
        Basket basket2 = Basket.builder().build();

        basketRepository.saveAll(Arrays.asList(basket1, basket2)).log().subscribe();

        BasketItem basketItem1 = BasketItem.builder()
                .basketId(basket1.getId())
                .build();

        BasketItem basketItem2 = BasketItem.builder()
                .basketId(basket1.getId())
                .build();

        // this BasketItem has the wrong basketId and should not be returned
        BasketItem basketItem3 = BasketItem.builder()
                .basketId(basket2.getId())
                .build();

        basketItemRepository.saveAll(Arrays.asList(basketItem1, basketItem2, basketItem3)).log().subscribe();

        StepVerifier.create(basketItemRepository.findByBasketId(basket1.getId()))
                .expectNext(basketItem1)
                .expectNext(basketItem2)
                .verifyComplete();
    }
}