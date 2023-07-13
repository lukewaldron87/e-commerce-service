package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.OrderItem;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderItemService {

    Flux<OrderItem> getOrderItemsForOrderId(Long orderId);
}
