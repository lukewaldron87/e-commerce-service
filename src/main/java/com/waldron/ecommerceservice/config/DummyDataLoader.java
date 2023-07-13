package com.waldron.ecommerceservice.config;

import com.waldron.ecommerceservice.entity.*;
import com.waldron.ecommerceservice.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;

@Configuration
public class DummyDataLoader {

    @Bean
    public CommandLineRunner loadProducts(ProductRepository repository) {

        return args -> repository.saveAll(Arrays.asList(
                Product.builder().name("Video Game").price(BigDecimal.valueOf(45.50)).build(),
                Product.builder().name("Mug").price(BigDecimal.valueOf(14.99)).build(),
                Product.builder().name("T-Shirt").price(BigDecimal.valueOf(30.00)).build(),
                Product.builder().name("Smart Phone").price(BigDecimal.valueOf(147.19)).build())
        ).blockLast(Duration.ofSeconds(10));
    }

    @Bean
    public CommandLineRunner loadBaskets(BasketRepository repository) {

        return args -> repository.saveAll(Arrays.asList(
                Basket.builder().build(),
                Basket.builder().build())
        ).blockLast(Duration.ofSeconds(10));
    }

    @Bean
    public CommandLineRunner loadBasketItems(BasketItemRepository repository) {

        return args -> repository.saveAll(Arrays.asList(
                BasketItem.builder().productId(1l).productCount(3).basketId(1l).build(),
                BasketItem.builder().productId(2l).productCount(2).basketId(1l).build(),
                BasketItem.builder().productId(3l).productCount(1).basketId(1l).build(),
                BasketItem.builder().productId(1l).productCount(2).basketId(2l).build(),
                BasketItem.builder().productId(2l).productCount(2).basketId(2l).build())
        ).blockLast(Duration.ofSeconds(10));
    }

    @Bean
    public CommandLineRunner loadOrders(OrderRepository repository){

        return args -> repository.saveAll(Arrays.asList(
                Order.builder().status(Status.SHIPPED).name("Luke Waldron").address("12 Nice Street, The Big City, Smallland, 1027B2").build(),
                Order.builder().status(Status.PREPARING).name("John Doe").address("Apartment 6, Green Square, Universityvill, Bigland, 685Tw8").build())
        ).blockLast(Duration.ofSeconds(10));
    }

    @Bean
    public CommandLineRunner loadOrderItems(OrderItemRepository repository){

        return args -> repository.saveAll(Arrays.asList(
                OrderItem.builder().orderId(1l).productId(1l).productCount(3).build(),
                OrderItem.builder().orderId(1l).productId(2l).productCount(2).build(),
                OrderItem.builder().orderId(1l).productId(3l).productCount(1).build(),
                OrderItem.builder().orderId(2l).productId(1l).productCount(2).build(),
                OrderItem.builder().orderId(2l).productId(2l).productCount(2).build())
        ).blockLast(Duration.ofSeconds(10));
    }
}
