package com.waldron.ecommerceservice.controller;

import com.waldron.ecommerceservice.entity.Basket;
import com.waldron.ecommerceservice.service.BasketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/baskets")
public class BasketController {

    @Autowired
    private BasketService basketService;

    // regression tested
    @GetMapping("/{id}")
    public Mono<Basket> getBasketForId(@PathVariable Long id){
        return basketService.getBasketForId(id);
    }

    // regression tested add new and add existing product
    @PatchMapping("/{basketId}/products/{productId}/quantity/{quantity}")
    public Mono<Basket> addProductToBasket(@PathVariable Long basketId,
                                           @PathVariable Long productId,
                                           @PathVariable int quantity){

        return basketService.addProductToBasket(basketId, productId, quantity);
    }
}
