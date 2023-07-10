package com.waldron.ecommerceservice.entity;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Data
@Builder
@ToString
public class Product {

    @Id
    private Long id;

    //todo move validation message to message file https://www.vinsguru.com/spring-webflux-validation/
    @NotNull(message = "Name can not be empty")
    private String name;

    @NotNull(message = "Product must have price")
    //todo add validator for price format
    private BigDecimal price;
}
