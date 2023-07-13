package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.Order;
import reactor.core.publisher.Mono;

public interface OrderService {
    Mono<Order> getOrderForId(Long orderId);

    //todo
    //getTotalPriceForOrderId
    //createOrderFromBasket
    //setOrderStatus
}
