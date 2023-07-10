package com.waldron.ecommerceservice.entity;


import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Data
@Builder
@ToString
public class Product {

    //todo add validation

    @Id
    private Long id;

    //@NotNull(message = "Name can not be empty")
    private String name;
    private BigDecimal price;
}
