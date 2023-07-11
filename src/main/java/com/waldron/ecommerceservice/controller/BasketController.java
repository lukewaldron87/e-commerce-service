package com.waldron.ecommerceservice.controller;

import com.waldron.ecommerceservice.entity.Basket;
import com.waldron.ecommerceservice.service.BasketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/baskets")
public class BasketController {

    @Autowired
    BasketService basketService;

    @GetMapping("/{id}")
    public Mono<Basket> getBasketForId(@PathVariable Long id){
        return basketService.getBasketForId(id);
    }

}
