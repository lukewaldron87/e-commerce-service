package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.dto.OrderDto;
import com.waldron.ecommerceservice.entity.Basket;
import com.waldron.ecommerceservice.entity.Order;

public interface OrderMapperService {

    Order mapDtoToNewEntity(OrderDto orderDto);

    void mapBasketToOrder(Basket sourceBasket, Order targetOrder);
}
