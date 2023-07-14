package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.Basket;
import com.waldron.ecommerceservice.entity.BasketItem;
import com.waldron.ecommerceservice.entity.Order;
import com.waldron.ecommerceservice.entity.OrderItem;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class BasketToOrderMapperServiceImplTest {

    @Mock
    private OrderItemServiceImpl orderItemService;

    @InjectMocks
    private BasketToOrderMapperServiceImpl basketToOrderMapperService;

    @Test
    public void mapBasketToOrder_shouldTranslateBasketItemsToOrderItems(){
        Long basketId = 1l;
        Long product1Id = 1l;
        Long product2Id = 2l;
        Basket basket = mockBasket(basketId, product1Id, product2Id);

        Long orderId = 1l;
        Order expectedOrder = mockExpectedOrder(product1Id, product2Id, orderId);

        Iterator<OrderItem> iterator = expectedOrder.getOrderItems().iterator();
        when(orderItemService.mapBasketItemToOrderItem(basket.getBasketItemForProductId(product1Id)))
                .thenReturn(iterator.next());
        when(orderItemService.mapBasketItemToOrderItem(basket.getBasketItemForProductId(product2Id)))
                .thenReturn(iterator.next());

        Order newOrder = Order.builder().id(orderId).build();

        basketToOrderMapperService.mapBasketToOrder(basket, newOrder);
        assertEquals(expectedOrder, newOrder);
    }

    /*@Test
    public void mapBasketToOrder_shouldAddOrderIdToOrderItems(){
        Long basketId = 1l;
        Long product1Id = 1l;
        Long product2Id = 2l;
        Basket basket = mockBasket(basketId, product1Id, product2Id);

        // return the order items without order Ids to test if they are set in mapBasketToOrder
        when(orderItemService.mapBasketItemToOrderItem(basket.getBasketItemForProductId(product1Id)))
                .thenReturn(OrderItem.builder().build());
        when(orderItemService.mapBasketItemToOrderItem(basket.getBasketItemForProductId(product2Id)))
                .thenReturn(OrderItem.builder().build());

        Long orderId = 1l;
        Order newOrder = Order.builder().id(orderId).build();

        basketToOrderMapperService.mapBasketToOrder(basket, newOrder);
        Iterator<OrderItem> iterator = newOrder.getOrderItems().iterator();
        assertEquals(orderId, iterator.next().getOrderId());
        assertEquals(orderId, iterator.next().getOrderId());

    }*/

    private static Order mockExpectedOrder(Long product1Id, Long product2Id, Long orderId) {
        OrderItem orderItem1 = OrderItem.builder()
                .id(1l)
                .orderId(orderId)
                .productId(product1Id)
                .productCount(1)
                .build();
        OrderItem orderItem2 = OrderItem.builder()
                .id(2l)
                .orderId(orderId)
                .productId(product2Id)
                .productCount(1)
                .build();

        Set<OrderItem> orderItems = new HashSet<>();
        orderItems.add(orderItem1);
        orderItems.add(orderItem2);

        Order expectedOrder = Order.builder()
                .id(orderId)
                .orderItems(orderItems)
                .build();
        return expectedOrder;
    }

    private static Basket mockBasket(Long basketId, Long product1Id, Long product2Id) {
        BasketItem basketItem1 = BasketItem.builder()
                .id(1l)
                .productId(product1Id)
                .productCount(1)
                .basketId(basketId)
                .build();
        BasketItem basketItem2 = BasketItem.builder()
                .id(2l)
                .productId(product2Id)
                .productCount(2)
                .basketId(basketId)
                .build();

        Map<Long, BasketItem> goodIdToBasketItemMap = new HashMap<>();
        goodIdToBasketItemMap.put(product1Id, basketItem1);
        goodIdToBasketItemMap.put(product2Id, basketItem2);

        Basket basket = Basket.builder()
                .id(basketId)
                .goodIdToBasketItemMap(goodIdToBasketItemMap)
                .build();
        return basket;
    }

}