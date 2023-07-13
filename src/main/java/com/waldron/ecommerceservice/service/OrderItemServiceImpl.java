package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.OrderItem;
import com.waldron.ecommerceservice.exception.NotFoundException;
import com.waldron.ecommerceservice.repository.OrderItemRepository;
import com.waldron.ecommerceservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public class OrderItemServiceImpl implements OrderItemService{

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Returns all order items associated with the given order
     *
     * @param orderId the ID of the order
     * @return all basket items associated with the given basket
     */
    @Override
    public Flux<OrderItem> getOrderItemsForOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId)
                .flatMap(orderItem -> {
                    if (orderItem.getProductId() == null) {
                        return Mono.just(orderItem);
                    }
                    return addProductToOrderItem(orderItem);
                });
    }

    //todo change to getProduct in service
    private Mono<OrderItem> addProductToOrderItem(OrderItem orderItem) {
        return productRepository.findById(orderItem.getProductId())
                .switchIfEmpty(Mono.error(new NotFoundException("Product not found")))
                .map(product ->
                {
                    orderItem.setProduct(product);
                    return orderItem;
                });
    }

    @Override
    public Mono<OrderItem> createOrderItem(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    @Override
    public Mono<Void> deleteOrderItemForId(Long orderItemId) {
        return orderItemRepository.deleteById(orderItemId);
    }

    @Override
    public BigDecimal getTotalPrice(OrderItem orderItem) {
        BigDecimal price = orderItem.getProduct().getPrice();
        int productCount = orderItem.getProductCount();
        return price.multiply(BigDecimal.valueOf(productCount));
    }
}
