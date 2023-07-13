package com.waldron.ecommerceservice.controller;

import com.waldron.ecommerceservice.entity.Order;
import com.waldron.ecommerceservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // manually tested
    @GetMapping("/{id}")
    public Mono<Order> getOrderForId(@PathVariable Long id){
        return orderService.getOrderForId(id);
    }
}
