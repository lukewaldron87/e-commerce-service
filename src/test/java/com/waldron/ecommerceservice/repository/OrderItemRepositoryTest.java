package com.waldron.ecommerceservice.repository;

import com.waldron.ecommerceservice.entity.Order;
import com.waldron.ecommerceservice.entity.OrderItem;
import com.waldron.ecommerceservice.entity.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.test.StepVerifier;

import java.util.Arrays;

@DataR2dbcTest
class OrderItemRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    public void findByOrderId_shouldReturnAllOrderItemsAssociatedToTheGivenOrderId(){

        Order order1 = Order.builder().status(Status.SHIPPED).name("name 1").address("address 1").build();
        Order order2 = Order.builder().status(Status.PREPARING).name("name 2").address("address 2").build();
        orderRepository.saveAll(Arrays.asList(order1, order2)).log().subscribe();

        OrderItem orderItem1 = OrderItem.builder().orderId(order1.getId()).build();
        OrderItem orderItem2 = OrderItem.builder().orderId(order1.getId()).build();
        // this OrderItem is associated to order 2 and should not be returned
        OrderItem orderItem3 = OrderItem.builder().orderId(order2.getId()).build();
        orderItemRepository.saveAll(Arrays.asList(orderItem1, orderItem2, orderItem3)).log().subscribe();

        StepVerifier.create(orderItemRepository.findByOrderId(order1.getId()))
                .expectNext(orderItem1)
                .expectNext(orderItem2)
                .verifyComplete();
    }

}