package com.waldron.ecommerceservice.controller;

import com.waldron.ecommerceservice.dto.OrderDto;
import com.waldron.ecommerceservice.entity.Order;
import com.waldron.ecommerceservice.entity.OrderItem;
import com.waldron.ecommerceservice.entity.Product;
import com.waldron.ecommerceservice.entity.Status;
import com.waldron.ecommerceservice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
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

    @Test
    public void getTotalPriceForOrderId_shouldGetBigDecimalFromService() {

        Long orderId = 1l;
        BigDecimal expectedPrice = BigDecimal.valueOf(19.99);

        when(orderService.getTotalPriceForOrderId(orderId)).thenReturn(Mono.just(expectedPrice));

        webClient.get().uri(ORDERS_URI+"/"+orderId+"/total")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BigDecimal.class)
                .isEqualTo(expectedPrice);
    }

    @Test
    public void createOrderFromBasket_shouldPassOrderAndIdToService(){

        OrderDto orderDto = new OrderDto(1l, "name", "address");

        Long orderId = 1l;
        Long productId = 1l;
        Product product = Product.builder().id(productId).name("name").price(BigDecimal.valueOf(13.99)).build();
        OrderItem orderItem = OrderItem.builder().id(1l).orderId(orderId).productId(productId).product(product).productCount(1).build();
        Order expectedOrder = Order.builder()
                .id(orderId)
                .name("name")
                .address("address")
                .orderItems(new HashSet<>(Arrays.asList(orderItem)))
                .status(Status.PREPARING).build();

        when(orderService.createOrderFromBasket(orderDto)).thenReturn(Mono.just(expectedOrder));

        webClient.post()
                .uri(ORDERS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(orderDto), OrderDto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Order.class)
                .isEqualTo(expectedOrder);

        ArgumentCaptor<OrderDto> orderDtoCaptor = ArgumentCaptor.forClass(OrderDto.class);
        verify(orderService).createOrderFromBasket(orderDtoCaptor.capture());
        assertEquals(orderDto, orderDtoCaptor);
    }

}