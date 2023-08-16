package com.waldron.ecommerceservice.web.controller;

import com.waldron.ecommerceservice.dto.BasketItemDto;
import com.waldron.ecommerceservice.entity.Basket;
import com.waldron.ecommerceservice.service.BasketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/baskets")
public class BasketController {

    @Autowired
    private BasketService basketService;

    @PatchMapping("/{basketId}/reduce")
    public Mono<Basket> reduceNumberOfProductsInBasket(@PathVariable Long basketId,
                                                       @Valid @RequestBody BasketItemDto basket){

        return basketService.reduceNumberOfProductsInBasket(basketId, basket.getProductId(), basket.getProductCount());
    }
}
