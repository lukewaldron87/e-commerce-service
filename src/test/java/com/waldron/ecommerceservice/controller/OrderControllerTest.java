package com.waldron.ecommerceservice.controller;

import com.waldron.ecommerceservice.entity.Order;
import com.waldron.ecommerceservice.entity.Status;
import com.waldron.ecommerceservice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@WebFluxTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private OrderService orderService;

    private static String ORDERS_URI = "/orders";

    @Test
    public void getOrderForId_shouldGetOrderFromService(){

        Long orderId = 1l;
        Order expectedOrder = Order.builder()
                .id(orderId)
                .status(Status.PREPARING)
                .name("name")
                .address("address").build();

        when(orderService.getOrderForId(orderId)).thenReturn(Mono.just(expectedOrder));

        webClient.get().uri(ORDERS_URI +"/"+orderId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Order.class)
                .isEqualTo(expectedOrder);
    }

}