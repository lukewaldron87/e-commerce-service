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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

        Map<Long, BasketItem> goodIdToBasketItemMap = new HashMap<>();

        basketItemService.getBasketItemsForBasketId(basketId).collectMap(
                basketItem -> basketItem.getProductId(),
                basketItem -> basketItem
        ).subscribe(goodIdToBasketItemMap::putAll);

        return basketMono.map(basket -> {basket.setGoodIdToBasketItemMap(goodIdToBasketItemMap); return basket;});
    }

    //todo get total

    //todo add x number of products

    //todo remove x number of products

    //todo remove product

    //todo add get Map <Product, Integer> productsToCountMap
}
