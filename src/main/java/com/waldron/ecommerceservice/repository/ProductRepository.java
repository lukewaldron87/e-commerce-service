package com.waldron.ecommerceservice.repository;

import com.waldron.ecommerceservice.entity.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {
}
