package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.Basket;
import com.waldron.ecommerceservice.entity.BasketItem;
import com.waldron.ecommerceservice.exception.NotFoundException;
import com.waldron.ecommerceservice.repository.BasketItemRepository;
import com.waldron.ecommerceservice.repository.BasketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BasketServiceImpl implements BasketService{

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private BasketItemService basketItemService;

    @Override
    public Mono<Basket> getBasketForId(Long basketId) {

        //todo refactor to functional solution

        Mono<Basket> basketMono = basketRepository.findById(basketId)
                .switchIfEmpty(Mono.error(new NotFoundException("Basket not found")));

        Set<BasketItem> basketItemSet = new HashSet<>();
        basketItemService.getBasketItemsForBasketId(basketId).collect(Collectors.toSet()).subscribe(basketItemSet::addAll);

        return basketMono.map(basket -> {basket.setBasketItems(basketItemSet); return basket;});
        // use basketItemService.getBasketItemsForBasketId to populate Set<BasketItem> basketItems
    }
}
