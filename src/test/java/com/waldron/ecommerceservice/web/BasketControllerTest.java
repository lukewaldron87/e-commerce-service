package com.waldron.ecommerceservice.web;

import com.waldron.ecommerceservice.dto.BasketDto;
import com.waldron.ecommerceservice.entity.Basket;
import com.waldron.ecommerceservice.service.BasketService;
import com.waldron.ecommerceservice.web.controller.BasketController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebFluxTest(BasketController.class)
class BasketControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private BasketService basketService;

    private static String BASKETS_URI = "/baskets";

    @Test
    public void createBasketForProduct_shouldPassDtoToService(){

        long productId = 1l;
        BasketDto basketDto = new BasketDto(productId, 1);

        webClient.post()
                .uri(BASKETS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(basketDto), BasketDto.class)
                .exchange()
                .expectStatus().isCreated();

        verify(basketService, times(1)).createBasketForProduct(any(BasketDto.class));
    }

    @Test
    public void createOrderFromBasket_shouldReturnOrderProvidedByService(){

        long productId = 1l;
        BasketDto basketDto = new BasketDto(productId, 1);

        Basket expectedBasket = Basket.builder().build();

        when(basketService.createBasketForProduct(any())).thenReturn(Mono.just(expectedBasket));

        webClient.post()
                .uri(BASKETS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(basketDto), BasketDto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Basket.class)
                .isEqualTo(expectedBasket);
    }

    @Test
    public void addNumberOfProductsToBasket_shouldGetBasketFromService(){
        Long basketId = 1l;
        Long productId = 1l;
        int quantity = 1;
        BasketDto basketDto = new BasketDto(productId, quantity);
        Basket expectedBasket = Basket.builder()
                .id(productId)
                .build();

        when(basketService.addNumberOfProductsToBasket(productId, productId, quantity))
                .thenReturn(Mono.just(expectedBasket));

        webClient.patch().uri(BASKETS_URI+"/"+basketId+"/add")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(basketDto), BasketDto.class)
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
        BasketDto basketDto = new BasketDto(productId, quantity);
        Basket expectedBasket = Basket.builder()
                .id(productId)
                .build();

        when(basketService.reduceNumberOfProductsInBasket(productId, productId, quantity))
                .thenReturn(Mono.just(expectedBasket));

        webClient.patch().uri(BASKETS_URI+"/"+basketId+"/reduce")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(basketDto), BasketDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Basket.class)
                .isEqualTo(expectedBasket);
    }
}