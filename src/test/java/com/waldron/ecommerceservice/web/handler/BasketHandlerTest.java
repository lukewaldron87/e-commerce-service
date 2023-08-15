package com.waldron.ecommerceservice.web.handler;

import com.waldron.ecommerceservice.config.BasketRouter;
import com.waldron.ecommerceservice.entity.Basket;
import com.waldron.ecommerceservice.service.BasketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BasketHandlerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private BasketService basketService;

    @Test
    public void getBasketForId_shouldGetBasketFromService(){

        Long basketId = 1l;
        Basket expectedBasket = Basket.builder()
                .id(basketId)
                .build();

        when(basketService.getBasketForId(basketId)).thenReturn(Mono.just(expectedBasket));

        webClient.get().uri(BasketRouter.BASKETS_URI+"/"+basketId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Basket.class)
                .isEqualTo(expectedBasket);
    }

    @Test
    public void getBasketForId_shouldReturnNotFound_whenEmptyMonoReturned(){

        Long basketId = 1l;

        when(basketService.getBasketForId(basketId)).thenReturn(Mono.empty());

        webClient.get().uri(BasketRouter.BASKETS_URI+"/"+basketId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void getTotalPriceForBasketId_shouldGetBigDecimalFromService(){

        Long basketId = 1l;
        BigDecimal expectedPrice = BigDecimal.valueOf(19.99);

        when(basketService.getTotalPriceForBasketId(basketId)).thenReturn(Mono.just(expectedPrice));

        webClient.get().uri(BasketRouter.BASKETS_URI+"/"+basketId+"/total")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BigDecimal.class)
                .isEqualTo(expectedPrice);
    }

    @Test
    public void getTotalPriceForBasketId_shouldReturnNotFound_whenEmptyMonoReturned() {
        Long basketId = 1l;

        when(basketService.getTotalPriceForBasketId(basketId)).thenReturn(Mono.empty());

        webClient.get().uri(BasketRouter.BASKETS_URI+"/"+basketId+"/total")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }
}