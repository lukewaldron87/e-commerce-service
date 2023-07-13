package com.waldron.ecommerceservice.controller;

import com.waldron.ecommerceservice.entity.Basket;
import com.waldron.ecommerceservice.service.BasketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(BasketController.class)
class BasketControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private BasketService basketService;

    private static String BASKETS_URI = "/baskets";


    @Test
    public void getBasketForId_shouldGetBasketFromService(){

        Long basketId = 1l;
        Basket expectedBasket = Basket.builder()
                .id(basketId)
                .build();

        when(basketService.getBasketForId(basketId)).thenReturn(Mono.just(expectedBasket));

        webClient.get().uri(BASKETS_URI+"/"+basketId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Basket.class)
                .isEqualTo(expectedBasket);
    }


    @Test
    public void getTotalPriceForBasketId_shouldGetBigDecimalFromService(){

        Long basketId = 1l;
        BigDecimal expectedPrice = BigDecimal.valueOf(19.99);

        when(basketService.getTotalPriceForBasketId(basketId)).thenReturn(Mono.just(expectedPrice));

        webClient.get().uri(BASKETS_URI+"/"+basketId+"/total")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BigDecimal.class)
                .isEqualTo(expectedPrice);
    }

    @Test
    public void addNumberOfProductsToBasket_shouldGetBasketFromService(){
        Long basketId = 1l;
        Long productId = 1l;
        int quantity = 1;
        Basket expectedBasket = Basket.builder()
                .id(productId)
                .build();

        when(basketService.addNumberOfProductsToBasket(productId, productId, quantity))
                .thenReturn(Mono.just(expectedBasket));

        webClient.patch().uri(BASKETS_URI+"/"+basketId+"/products/"+productId+"/quantity/"+quantity+"/add")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Basket.class)
                .isEqualTo(expectedBasket);
    }

    @Test
    public void reduceNumberOfProductsInBasket_shouldGetBasketFromService(){
        Long basketId = 1l;
        Long productId = 1l;
        int quantity = 1;
        Basket expectedBasket = Basket.builder()
                .id(productId)
                .build();

        when(basketService.reduceNumberOfProductsInBasket(productId, productId, quantity))
                .thenReturn(Mono.just(expectedBasket));

        webClient.patch().uri(BASKETS_URI+"/"+basketId+"/products/"+productId+"/quantity/"+quantity+"/reduce")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Basket.class)
                .isEqualTo(expectedBasket);
    }
}