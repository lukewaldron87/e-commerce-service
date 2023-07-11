package com.waldron.ecommerceservice.entity;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;

@Data
@Builder
@ToString
@Table("basket")
public class Basket {

    @Id
    private Long id;

    @Transient
    private Set<BasketItem> basketItems;

}
