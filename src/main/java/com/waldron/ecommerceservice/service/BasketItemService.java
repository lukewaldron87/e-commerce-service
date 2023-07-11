package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.BasketItem;
import com.waldron.ecommerceservice.entity.Product;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface BasketItemService {

    Mono<BasketItem> getBasketItemForId(Long basketItemId);

    Mono<BasketItem> createBasketItem(BasketItem basketItem);

    Mono<Void> deleteBasketItemForId(Long basketItemId);
}
