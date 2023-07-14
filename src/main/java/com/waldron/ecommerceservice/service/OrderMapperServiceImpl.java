package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.dto.OrderDto;
import com.waldron.ecommerceservice.entity.Basket;
import com.waldron.ecommerceservice.entity.Order;
import com.waldron.ecommerceservice.entity.OrderItem;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderMapperServiceImpl implements OrderMapperService {

    @Autowired
    private OrderItemServiceImpl orderItemService;

    @Override
    public Order mapDtoToNewEntity(OrderDto orderDto) {
        Order orderEntity = Order.builder().build();
        BeanUtils.copyProperties(orderDto, orderEntity);
        return orderEntity;
    }

    /**
     * Maps a basket an order creating OrderItems for all BasketItems
     *
     * The targetOrder must contain an id to set the orderId in each OrderItem.
     * This enables the relationship between Order and OrderItem
     *
     * @param sourceBasket the Basket to copy
     * @param targetOrder the Order to copy to
     */
    @Override
    public void mapBasketToOrder(Basket sourceBasket, Order targetOrder) {

        Set<OrderItem> orderItems = sourceBasket.getGoodIdToBasketItemMap().values().stream()
                .map(basketItem -> orderItemService.mapBasketItemToOrderItem(basketItem))
                .map(orderItem -> {
                    orderItem.setOrderId(targetOrder.getId());
                    return orderItem;
                })
                .collect(Collectors.toSet());

        targetOrder.setOrderItems(orderItems);
    }
}
