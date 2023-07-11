package com.waldron.ecommerceservice.repository;

import com.waldron.ecommerceservice.entity.BasketItem;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BasketItemRepository extends ReactiveCrudRepository<BasketItem, Long> {
}
