package com.waldron.ecommerceservice.repository;

import com.waldron.ecommerceservice.entity.Order;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {
}
