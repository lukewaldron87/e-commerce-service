package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.Basket;
import reactor.core.publisher.Mono;

public interface BasketService {

    Mono<Basket> getBasketForId(Long basketId);
}
