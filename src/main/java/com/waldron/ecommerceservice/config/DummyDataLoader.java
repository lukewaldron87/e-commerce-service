package com.waldron.ecommerceservice.config;

import com.waldron.ecommerceservice.entity.Product;
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
    public CommandLineRunner loadDummyData(ProductRepository repository) {

        return (args) -> {
            repository.saveAll(Arrays.asList(
                            Product.builder().name("Video Game").price(BigDecimal.valueOf(45.50)).build(),
                            Product.builder().name("Mug").price(BigDecimal.valueOf(14.99)).build()))
                    .blockLast(Duration.ofSeconds(10));


        };
    }
}
