package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.Basket;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface BasketService {

    Mono<Basket> getBasketForId(Long basketId);

    Mono<Basket> addNumberOfProductsToBasket(Long basketId, Long productId, int numberOfProducts);

    Mono<Basket> reduceNumberOfProductsInBasket(Long basketId, Long productId, int numberOfProducts);

    Mono<BigDecimal> getTotalPriceForBasketId(Long basketId);

    //todo delete Basket (should call deleteBasketItemsForBasketId)
}
