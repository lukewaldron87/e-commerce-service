package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.OrderItem;
import com.waldron.ecommerceservice.entity.Product;
import com.waldron.ecommerceservice.repository.OrderItemRepository;
import com.waldron.ecommerceservice.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class OrderItemServiceImplTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

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

    @Test
    public void getOrderItemsForOrderId_shouldAddCorrectProductToOrderItem(){

        Long orderId = 1l;
        Long product1Id = 1l;
        Long product2Id = 1l;

        OrderItem orderItem1 = OrderItem.builder().orderId(orderId).productId(product1Id).build();
        OrderItem orderItem2 = OrderItem.builder().orderId(orderId).productId(product2Id).build();

        Product product1 = Product.builder().id(product1Id).build();
        OrderItem expectedOrderItem1 = OrderItem.builder().orderId(orderId).productId(product1.getId()).product(product1).build();
        Product product2 = Product.builder().id(product2Id).build();
        OrderItem expectedOrderItem2 = OrderItem.builder().orderId(orderId).productId(product2.getId()).product(product2).build();

        when(orderItemRepository.findByOrderId(orderId)).thenReturn(Flux.just(orderItem1, orderItem2));
        when(productRepository.findById(product1Id)).thenReturn(Mono.just(product1));
        when(productRepository.findById(product2Id)).thenReturn(Mono.just(product2));

        StepVerifier.create(orderItemService.getOrderItemsForOrderId(orderId))
                .expectNext(expectedOrderItem1)
                .expectNext(expectedOrderItem2)
                .verifyComplete();

    }


}