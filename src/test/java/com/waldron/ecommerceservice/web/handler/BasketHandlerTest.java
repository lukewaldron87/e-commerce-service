package com.waldron.ecommerceservice.web.handler;

import com.waldron.ecommerceservice.config.BasketRouter;
import com.waldron.ecommerceservice.dto.BasketItemDto;
import com.waldron.ecommerceservice.entity.Basket;
import com.waldron.ecommerceservice.service.BasketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    //todo fix test
    /*@Test
    public void createBasketForProduct_shouldPassDtoToService(){

        long productId = 1l;
        BasketDto basketDto = new BasketDto(productId, 1);

        webClient.post()
                .uri(BasketRouter.BASKETS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(basketDto), BasketDto.class)
                .exchange()
                .expectStatus().isCreated();

        verify(basketService, times(1)).createBasketForProduct(any(Mono.class));
    }*/

    @Test
    public void createOrderFromBasket_shouldReturnOrderProvidedByService(){

        long productId = 1l;
        BasketItemDto basketItemDto = new BasketItemDto(productId, 1);

        Basket expectedBasket = Basket.builder().build();

        when(basketService.createBasketForProduct(any())).thenReturn(Mono.just(expectedBasket));

        webClient.post()
                .uri(BasketRouter.BASKETS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(basketItemDto), BasketItemDto.class)
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
        BasketItemDto basketItemDto = new BasketItemDto(productId, quantity);
        Basket expectedBasket = Basket.builder()
                .id(productId)
                .build();

        when(basketService.addNumberOfProductsToBasket(productId, productId, quantity))
                .thenReturn(Mono.just(expectedBasket));

        webClient.patch().uri(BasketRouter.BASKETS_URI+"/"+basketId+"/add")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(basketItemDto), BasketItemDto.class)
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
        BasketItemDto basketItemDto = new BasketItemDto(productId, quantity);
        Basket expectedBasket = Basket.builder()
                .id(productId)
                .build();

        when(basketService.reduceNumberOfProductsInBasket(productId, productId, quantity))
                .thenReturn(Mono.just(expectedBasket));

        webClient.patch().uri(BasketRouter.BASKETS_URI+"/"+basketId+"/reduce")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(basketItemDto), BasketItemDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Basket.class)
                .isEqualTo(expectedBasket);
    }
}