package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.OrderItem;
import com.waldron.ecommerceservice.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class OrderItemServiceImpl implements OrderItemService{

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Override
    public Flux<OrderItem> getOrderItemsForOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }
}
