package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.BasketItem;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface BasketItemService {

    Mono<BasketItem> getBasketItemForId(Long basketItemId);

    Flux<BasketItem> getBasketItemsForBasketId(Long basketId);

    Mono<BasketItem> createBasketItem(BasketItem basketItem);

    Mono<BasketItem> updatedBasketItem(BasketItem updatedBasketItem);

    Mono<BasketItem> addNumberOfProducts(BasketItem basketItem, int numberOfProducts);

    Mono<BasketItem> reduceNumberOfProducts(BasketItem basketItem, int numberOfProducts);

    Mono<Void> deleteBasketItemForId(Long basketItemId);

    Mono<Void> deleteBasketItemsForBasketId(Long basketId);

    BigDecimal getTotalPrice(BasketItem basketItem);
}
