package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.Order;
import com.waldron.ecommerceservice.exception.NotFoundException;
import com.waldron.ecommerceservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    public void getOrderForId_shouldGetOrderForGivenId(){

        Long orderId = 1l;
        Order expectedOrder = Order.builder().id(orderId).build();

        when(orderRepository.findById(orderId)).thenReturn(Mono.just(expectedOrder));

        StepVerifier.create(orderService.getOrderForId(orderId))
                .expectNext(expectedOrder)
                .verifyComplete();
    }

    @Test
    public void getOrderForId_shouldReturnErrorIfOrderNotFound(){
        Long orderId = 1l;

        when(orderRepository.findById(orderId)).thenReturn(Mono.empty());

        StepVerifier.create(orderService.getOrderForId(orderId))
                .expectError(NotFoundException.class)
                .verify();
    }
}