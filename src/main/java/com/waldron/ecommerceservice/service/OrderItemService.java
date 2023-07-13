package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.OrderItem;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface OrderItemService {

    Flux<OrderItem> getOrderItemsForOrderId(Long orderId);

    Mono<OrderItem> createOrderItem(OrderItem newOrderItem);

    //todo createOrderItemFromBasketItem

    Mono<Void> deleteOrderItemForId(Long orderItemId);

    BigDecimal getTotalPrice(OrderItem orderItem);
}
