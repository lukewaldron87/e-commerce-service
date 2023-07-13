package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.Order;
import com.waldron.ecommerceservice.exception.NotFoundException;
import com.waldron.ecommerceservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Mono<Order> getOrderForId(Long orderId) {
        return orderRepository.findById(orderId)
                .switchIfEmpty(Mono.error(new NotFoundException("Basket not found")));
    }
}
