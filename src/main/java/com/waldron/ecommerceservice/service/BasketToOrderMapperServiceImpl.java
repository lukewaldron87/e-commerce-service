package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.Basket;
import com.waldron.ecommerceservice.entity.Order;
import com.waldron.ecommerceservice.entity.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BasketToOrderMapperServiceImpl implements BasketToOrderMapperService{

    @Autowired
    private OrderItemServiceImpl orderItemService;

    @Override
    public void mapBasketToOrder(Basket sourceBasket, Order targetOrder) {

        Set<OrderItem> orderItems = sourceBasket.getGoodIdToBasketItemMap().values().stream()
                .map(basketItem -> orderItemService.mapBasketItemToOrderItem(basketItem))
                // set the order ids
                .map(orderItem -> {
                    orderItem.setOrderId(targetOrder.getId());
                    return orderItem;
                })
                .collect(Collectors.toSet());

        targetOrder.setOrderItems(orderItems);
    }
}
