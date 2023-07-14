package com.waldron.ecommerceservice.repository;

import com.waldron.ecommerceservice.entity.BasketItem;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BasketItemRepository extends ReactiveCrudRepository<BasketItem, Long> {

    @Query("SELECT id, product_id, product_count, basket_id FROM basket_item WHERE basket_id = :basketId")
    Flux<BasketItem> findByBasketId(Long basketId);

    @Query("DELETE FROM basket_item WHERE basket_id = :basketId")
    Mono<Void> deleteByBasketId(Long id);

}
