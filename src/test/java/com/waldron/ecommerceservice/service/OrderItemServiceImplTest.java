package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.OrderItem;
import com.waldron.ecommerceservice.repository.OrderItemRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class OrderItemServiceImplTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    @Test
    public void getOrderItemsForOrderId_shouldReturnAFluxOfOrderItems(){

        Long orderId = 1l;

        OrderItem orderItem1 = OrderItem.builder().build();
        OrderItem orderItem2 = OrderItem.builder().build();

        when(orderItemRepository.findByOrderId(orderId)).thenReturn(Flux.just(orderItem1, orderItem2));

        StepVerifier.create(orderItemService.getOrderItemsForOrderId(orderId))
                .expectNext(orderItem1)
                .expectNext(orderItem2)
                .verifyComplete();
    }

}