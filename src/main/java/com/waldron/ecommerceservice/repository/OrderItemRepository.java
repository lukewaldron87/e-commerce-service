package com.waldron.ecommerceservice.repository;


import com.waldron.ecommerceservice.entity.OrderItem;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface OrderItemRepository extends ReactiveCrudRepository<OrderItem, Long> {
}
