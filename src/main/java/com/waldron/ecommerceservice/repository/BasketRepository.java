package com.waldron.ecommerceservice.repository;

import com.waldron.ecommerceservice.entity.Basket;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BasketRepository extends ReactiveCrudRepository<Basket, Long> {
}
