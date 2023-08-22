package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.dto.OrderDto;
import com.waldron.ecommerceservice.entity.Order;
import com.waldron.ecommerceservice.entity.Status;
import com.waldron.ecommerceservice.exception.NotFoundException;
import com.waldron.ecommerceservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

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

        return orderRepository.findById(orderId)
                .switchIfEmpty(Mono.error(new NotFoundException("Basket not found")))
                .flatMap(order -> addOrderItemsToOrder(order));
    }

    private Mono<Order> addOrderItemsToOrder(Order order) {

        return orderItemService.getOrderItemsForOrderId(order.getId())
                .collect(Collectors.toSet())
                .zipWith(Mono.just(order))
                .map(tuple2 -> {
                    tuple2.getT2().setOrderItems(tuple2.getT1());
                    return tuple2.getT2();
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

        //todo refactor to cleaner solution

        AtomicLong basketId = new AtomicLong(orderDto.getBasketId());

        return createNewOrderFromDto(orderDto)
                .zipWith(basketService.getBasketForId(orderDto.getBasketId()))
                .map(tuple2 -> {
                    orderMapperService.mapBasketToOrder(tuple2.getT2(), tuple2.getT1());
                    return tuple2.getT1();
                })
                .map(order -> {
                            // create new order items
                    return Flux.just(order.getOrderItems())
                            .flatMap(Flux::fromIterable)
                            .flatMap(orderItemService::createOrderItem)
                            // delete basket
                            .then(basketService.deleteBasketForId(basketId.get()))
                            // return new order
                            .thenReturn(order);
                })
                .flatMap(orderMono -> orderMono);
    }

    /**
     * map dto to entity and save new order
     *
     * @param orderDto
     * @return
     */
    private Mono<Order> createNewOrderFromDto(OrderDto orderDto) {
        return Mono.just(orderMapperService.mapDtoToNewEntity(orderDto))
                .map(order -> {
                    order.setStatus(Status.PREPARING);
                    return order;
                })
                .flatMap(orderRepository::save);
    }
}
