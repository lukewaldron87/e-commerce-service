package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.Basket;
import com.waldron.ecommerceservice.repository.BasketRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class BasketServiceImplTest {

    @Mock
    private BasketRepository basketRepository;

    @InjectMocks
    private BasketServiceImpl basketService;

    @Test
    public void getBasketForId_shouldReturnFoundBasket(){

        Long basketId = 1l;
        Basket expectedBasket = Basket.builder()
                .id(basketId)
                .build();

        when(basketRepository.findById(basketId)).thenReturn(Mono.just(expectedBasket));

        StepVerifier.create(basketService.getBasketForId(basketId))
                .expectNext(expectedBasket)
                .verifyComplete();
    }

    @Test
    public void getBasketForId_shouldPopulateBasketItems_whenBasketItemsArePresent(){

    }

}