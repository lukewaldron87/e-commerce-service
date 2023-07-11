package com.waldron.ecommerceservice.config;

import com.waldron.ecommerceservice.entity.BasketItem;
import com.waldron.ecommerceservice.entity.Product;
import com.waldron.ecommerceservice.repository.BasketItemRepository;
import com.waldron.ecommerceservice.repository.ProductRepository;
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

        return (args) -> {
            repository.saveAll(Arrays.asList(
                            Product.builder().name("Video Game").price(BigDecimal.valueOf(45.50)).build(),
                            Product.builder().name("Mug").price(BigDecimal.valueOf(14.99)).build(),
                            Product.builder().name("T-Shirt").price(BigDecimal.valueOf(30.00)).build(),
                            Product.builder().name("Smart Phone").price(BigDecimal.valueOf(147.19)).build())
                    ).blockLast(Duration.ofSeconds(10));
        };
    }

    @Bean
    public CommandLineRunner loadBasketItems(BasketItemRepository repository) {

        return (args) -> {
            repository.saveAll(Arrays.asList(
                    BasketItem.builder().productId(1l).productCount(3).build(),
                    BasketItem.builder().productId(2l).productCount(2).build(),
                    BasketItem.builder().productId(3l).productCount(1).build())
            ).blockLast(Duration.ofSeconds(10));
        };
    }
}
