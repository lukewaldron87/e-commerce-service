package com.waldron.ecommerceservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@ToString
@Table("basket")
public class Basket {

    @Id
    private Long id;

    @Transient
    private Map<Long, BasketItem> goodIdToBasketItemMap;

    //this constructor allows reading from the database while ignoring the @Transient fields
    @PersistenceCreator
    public Basket(Long id){
        this.id = id;
    }

    //todo return immutable objects or deep copies
    public void addBasketItemForProductId(Long productId, BasketItem basketItem){
        initMapIfEmpty();
        goodIdToBasketItemMap.put(productId, basketItem);
    }

    private void initMapIfEmpty() {
        if(goodIdToBasketItemMap == null){
            goodIdToBasketItemMap = new HashMap<>();
        }
    }

    public BasketItem getBasketItemForProductId(Long productId){
        return goodIdToBasketItemMap.get(productId);
    }

    public boolean isProductInBasket(Long productId){
        return goodIdToBasketItemMap.containsKey(productId);
    }

    public BasketItem removeBasketItem(Long productId){
        return goodIdToBasketItemMap.remove(productId);
    }

}
