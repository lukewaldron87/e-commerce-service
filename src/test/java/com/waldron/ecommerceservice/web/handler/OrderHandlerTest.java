package com.waldron.ecommerceservice.web.handler;

import com.waldron.ecommerceservice.config.OrderRouter;
import com.waldron.ecommerceservice.dto.OrderDto;
import com.waldron.ecommerceservice.entity.Order;
import com.waldron.ecommerceservice.entity.Status;
import com.waldron.ecommerceservice.service.OrderService;
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
class OrderHandlerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private OrderService orderService;

    @Test
    public void getOrderForId_shouldGetOrderFromService(){

        Long orderId = 1l;
        Order expectedOrder = Order.builder()
                .id(orderId)
                .status(Status.PREPARING)
                .name("name")
                .address("address").build();

        when(orderService.getOrderForId(orderId)).thenReturn(Mono.just(expectedOrder));

        webClient.get().uri(OrderRouter.ORDERS_URI +"/"+orderId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Order.class)
                .isEqualTo(expectedOrder);
    }

    @Test
    public void getTotalPriceForOrderId_shouldGetBigDecimalFromService() {

        Long orderId = 1l;
        BigDecimal expectedPrice = BigDecimal.valueOf(19.99);

        when(orderService.getTotalPriceForOrderId(orderId)).thenReturn(Mono.just(expectedPrice));

        webClient.get().uri(OrderRouter.ORDERS_URI+"/"+orderId+"/total")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BigDecimal.class)
                .isEqualTo(expectedPrice);
    }

    @Test
    public void createOrderFromBasket_shouldPassOrderAndIdToService(){

        OrderDto orderDto = new OrderDto(1l, "name", "address");

        when(orderService.createOrderFromBasket(any(OrderDto.class))).thenReturn(Mono.just(Order.builder().build()));

        webClient.post()
                .uri(OrderRouter.ORDERS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(orderDto), OrderDto.class)
                .exchange()
                .expectStatus().isCreated();

        verify(orderService, times(1)).createOrderFromBasket(any(OrderDto.class));
    }

    @Test
    public void createOrderFromBasket_shouldReturnOrderProvidedByService(){

        OrderDto orderDto = new OrderDto(1l, "name", "address");

        Order expectedOrder = Order.builder().build();

        when(orderService.createOrderFromBasket(any())).thenReturn(Mono.just(expectedOrder));

        webClient.post()
                .uri(OrderRouter.ORDERS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(orderDto), OrderDto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Order.class)
                .isEqualTo(expectedOrder);
    }

}