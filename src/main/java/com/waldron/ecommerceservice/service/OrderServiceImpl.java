package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.Order;
import com.waldron.ecommerceservice.entity.OrderItem;
import com.waldron.ecommerceservice.exception.NotFoundException;
import com.waldron.ecommerceservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemService orderItemService;

    @Override
    public Mono<Order> getOrderForId(Long orderId) {
        //todo refactor to functional solution

        Mono<Order> orderMono = orderRepository.findById(orderId)
                .switchIfEmpty(Mono.error(new NotFoundException("Basket not found")));

        Set<OrderItem> orderItems = new HashSet<>();

        orderItemService.getOrderItemsForOrderId(orderId)
                .map(orderItem -> orderItems.add(orderItem)).subscribe();

        return orderMono.map(order -> {
            order.setOrderItems(orderItems);
            return order;
        });
    }

    @Override
    public Mono<BigDecimal> getTotalPriceForOrderId(Long orderId) {
        return getOrderForId(orderId)
                .map(basket -> basket.getOrderItems().stream()
                        .map(basketItem -> orderItemService.getTotalPrice(basketItem))
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
    }
}
