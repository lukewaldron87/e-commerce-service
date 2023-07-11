package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.Basket;
import com.waldron.ecommerceservice.repository.BasketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class BasketServiceImpl implements BasketService{

    @Autowired
    BasketRepository basketRepository;

    @Override
    public Mono<Basket> getBasketForId(Long basketId) {
        return basketRepository.findById(basketId);
    }
}
