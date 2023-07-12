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
    //todo check if /add is okay
    @PatchMapping("/{basketId}/products/{productId}/quantity/{quantity}/add")
    public Mono<Basket> addNumberOfProductsToBasket(@PathVariable Long basketId,
                                                    @PathVariable Long productId,
                                                    @PathVariable int quantity){

        return basketService.addNumberOfProductsToBasket(basketId, productId, quantity);
    }

    // regression tested remove and reduce
    //todo check if /reduce is okay
    @PatchMapping("/{basketId}/products/{productId}/quantity/{quantity}/reduce")
    public Mono<Basket> reduceNumberOfProductsInBasket(@PathVariable Long basketId,
                                                       @PathVariable Long productId,
                                                       @PathVariable int quantity){

        return basketService.reduceNumberOfProductsInBasket(basketId, productId, quantity);
    }
}
