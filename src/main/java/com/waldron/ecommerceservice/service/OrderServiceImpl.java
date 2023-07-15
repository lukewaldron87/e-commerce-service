package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.dto.OrderDto;
import com.waldron.ecommerceservice.entity.Basket;
import com.waldron.ecommerceservice.entity.Order;
import com.waldron.ecommerceservice.entity.OrderItem;
import com.waldron.ecommerceservice.entity.Status;
import com.waldron.ecommerceservice.exception.NotFoundException;
import com.waldron.ecommerceservice.repository.OrderRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private BasketService basketService;

    @Autowired
    private OrderMapperService orderMapperService;

    @Override
    public Mono<Order> getOrderForId(Long orderId) {

        Mono<Order> orderMono = orderRepository.findById(orderId)
                .switchIfEmpty(Mono.error(new NotFoundException("Basket not found")));

        return addOrderItemsToOrder(orderId, orderMono);
    }

    private Mono<Order> addOrderItemsToOrder(Long orderId, Mono<Order> orderMono) {
        Set<OrderItem> orderItems = new HashSet<>();

        orderItemService.getOrderItemsForOrderId(orderId)
                .map(orderItem -> orderItems.add(orderItem)).subscribe();

        return orderMono.map(order -> {
            order.setOrderItems(orderItems);
            return order;
        });
    }

    @Override
    public Mono<BigDecimal> getTotalPriceForOrderId(Long orderId) {
        return getOrderForId(orderId)
                .map(basket -> basket.getOrderItems().stream()
                        .map(basketItem -> orderItemService.getTotalPrice(basketItem))
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    /**
     * Creates an Order and Order Items for a given basket then deleted the basket and basket items.
     *
     * @param orderDto
     * @return
     */
    @Override
    public Mono<Order> createOrderFromBasket(OrderDto orderDto) {

        Order newOrder = createNewOrder(orderDto);

        Mono<Basket> basket = basketService.getBasketForId(orderDto.getBasketId());

        mergeBasketWithOrder(newOrder, basket);

        basket.flatMap(basketToDelete -> basketService.deleteBasketForId(basketToDelete.getId())).subscribe();

        return Mono.just(newOrder);
    }

    private void mergeBasketWithOrder(Order newOrder, Mono<Basket> basket) {
        // merge basket with newOrder
        basket.doOnNext(basketToMap -> orderMapperService.mapBasketToOrder(basketToMap, newOrder)).subscribe();
        // save orderItems
        newOrder.getOrderItems().stream()
                .forEach(orderItem -> orderItemService.createOrderItem(orderItem).subscribe());
    }

    private Order createNewOrder(OrderDto orderDto) {
        Order newOrder = orderMapperService.mapDtoToNewEntity(orderDto);
        newOrder.setStatus(Status.PREPARING);
        Mono<Order> orderMono = orderRepository.save(newOrder);
        orderMono.subscribe();
        return newOrder;
    }
}
