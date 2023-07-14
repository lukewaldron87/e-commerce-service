package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.Basket;
import com.waldron.ecommerceservice.entity.Order;

public interface BasketToOrderMapperService {

    void mapBasketToOrder(Basket sourceBasket, Order targetOrder);
}
