package com.waldron.ecommerceservice.controller;

import com.waldron.ecommerceservice.dto.OrderDto;
import com.waldron.ecommerceservice.entity.Order;
import com.waldron.ecommerceservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/{id}")
    public Mono<Order> getOrderForId(@PathVariable Long id){
        return orderService.getOrderForId(id);
    }

    @GetMapping("/{id}/total")
    public Mono<BigDecimal> getTotalPriceForOrderId(@PathVariable Long id){
        return orderService.getTotalPriceForOrderId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Order> createOrderFromBasket(@Valid @RequestBody OrderDto order) {
        //todo why is it returning an error
        return orderService.createOrderFromBasket(order);
    }
}
