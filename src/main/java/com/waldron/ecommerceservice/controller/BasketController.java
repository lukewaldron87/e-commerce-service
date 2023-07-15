package com.waldron.ecommerceservice.controller;

import com.waldron.ecommerceservice.dto.BasketDto;
import com.waldron.ecommerceservice.entity.Basket;
import com.waldron.ecommerceservice.service.BasketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
@RequestMapping("/baskets")
public class BasketController {

    @Autowired
    private BasketService basketService;

    @GetMapping("/{id}")
    public Mono<Basket> getBasketForId(@PathVariable Long id){
        return basketService.getBasketForId(id);
    }

    // todo improve the output eg {total price: 19.99}
    @GetMapping("/{id}/total")
    public Mono<BigDecimal> getTotalPriceForBasketId(@PathVariable Long id){
        return basketService.getTotalPriceForBasketId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Basket> createBasketForProduct(@Valid @RequestBody BasketDto basket){
        return basketService.createBasketForProduct(basket);
    }

    @PatchMapping("/{basketId}/add")
    public Mono<Basket> addNumberOfProductsToBasket(@PathVariable Long basketId,
                                                    @Valid @RequestBody BasketDto basket){
        return basketService.addNumberOfProductsToBasket(basketId, basket.getProductId(), basket.getProductCount());
    }

    @PatchMapping("/{basketId}/reduce")
    public Mono<Basket> reduceNumberOfProductsInBasket(@PathVariable Long basketId,
                                                       @Valid @RequestBody BasketDto basket){

        return basketService.reduceNumberOfProductsInBasket(basketId, basket.getProductId(), basket.getProductCount());
    }
}
