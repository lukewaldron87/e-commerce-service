package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.Order;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface OrderService {
    Mono<Order> getOrderForId(Long orderId);

    Mono<BigDecimal> getTotalPriceForOrderId(Long orderId);

    //todo
    //createOrderFromBasket (REST PUT/orders/baskets/{basketId} then add the rest in the body) Should delete basket at the end
    //setOrderStatus (nice to have)
}
