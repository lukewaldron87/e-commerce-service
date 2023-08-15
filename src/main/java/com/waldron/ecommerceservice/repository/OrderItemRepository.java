package com.waldron.ecommerceservice.repository;


import com.waldron.ecommerceservice.entity.OrderItem;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface OrderItemRepository extends ReactiveCrudRepository<OrderItem, Long> {

    @Query("SELECT ID, product_id, product_count, order_id FROM order_item WHERE order_id = :orderId")
    Flux<OrderItem> findByOrderId(Long orderId);
}
