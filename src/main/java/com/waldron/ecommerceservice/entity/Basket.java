package com.waldron.ecommerceservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@ToString
@Table("basket")
public class Basket {

    @Id
    private Long id;

    @Transient
    private Set<BasketItem> basketItems;

    //this constructor allows reading from the database while ignoring the @Transient fields
    @PersistenceConstructor
    public Basket(Long id){
        this.id = id;
    }

}
