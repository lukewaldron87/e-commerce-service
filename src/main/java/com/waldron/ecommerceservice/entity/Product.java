package com.waldron.ecommerceservice.entity;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@Builder
@ToString
@Table("product")
public class Product {

    @Id
    private Long id;

    @NotNull(message = "Name can not be empty")
    private String name;

    @NotNull(message = "Product must have price")
    private BigDecimal price;
}
