package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.dto.OrderDto;
import com.waldron.ecommerceservice.entity.Order;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface OrderService {

    Mono<Order> getOrderForId(Long orderId);

    Mono<BigDecimal> getTotalPriceForOrderId(Long orderId);

    Mono<Order> createOrderFromBasket(OrderDto newOrder);

}
