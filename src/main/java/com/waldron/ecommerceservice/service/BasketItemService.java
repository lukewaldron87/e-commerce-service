package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.BasketItem;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface BasketItemService {

    Mono<BasketItem> getBasketItemForId(Long basketItemId);

    Mono<BasketItem> createBasketItem(BasketItem basketItem);

    Mono<BasketItem> updatedBasketItem(BasketItem updatedBasketItem);

    Mono<Void> deleteBasketItemForId(Long basketItemId);
}
