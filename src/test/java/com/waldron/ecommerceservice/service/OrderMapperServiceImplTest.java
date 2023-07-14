package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.dto.OrderDto;
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
class OrderMapperServiceImplTest {

    @Mock
    private OrderItemServiceImpl orderItemService;

    @InjectMocks
    private OrderMapperServiceImpl orderMapperService;

    @Test
    public void mapDtoToNewEntity_mapDtoToEntity(){

        Long basketId = 1l;
        String name = "name";
        String address = "address";
        OrderDto orderDto = new OrderDto(basketId, name, address);

        Order order = orderMapperService.mapDtoToNewEntity(orderDto);
        assertEquals(name, order.getName());
        assertEquals(address, order.getAddress());
    }

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

        orderMapperService.mapBasketToOrder(basket, newOrder);
        assertEquals(expectedOrder, newOrder);
    }

    @Test
    public void mapBasketToOrder_shouldAddOrderIdToOrderItems(){
        Long basketId = 1l;
        Long product1Id = 1l;
        Long product2Id = 2l;
        Basket basket = mockBasket(basketId, product1Id, product2Id);

        Long orderId = 1l;
        Order expectedOrder = mockExpectedOrder(product1Id, product2Id, orderId);
        // return the order items without order Ids to test if they are set in mapBasketToOrder
        Iterator<OrderItem> iterator = expectedOrder.getOrderItems().iterator();
        OrderItem orderItem1 = iterator.next();
        orderItem1.setOrderId(null);
        OrderItem orderItem2 = iterator.next();
        orderItem2.setOrderId(null);
        when(orderItemService.mapBasketItemToOrderItem(basket.getBasketItemForProductId(product1Id)))
                .thenReturn(orderItem1);
        when(orderItemService.mapBasketItemToOrderItem(basket.getBasketItemForProductId(product2Id)))
                .thenReturn(orderItem2);

        Order newOrder = Order.builder().id(orderId).build();

        orderMapperService.mapBasketToOrder(basket, newOrder);
        Iterator<OrderItem> newOrderIterator = newOrder.getOrderItems().iterator();
        assertEquals(orderId, newOrderIterator.next().getOrderId());
        assertEquals(orderId, newOrderIterator.next().getOrderId());

    }

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