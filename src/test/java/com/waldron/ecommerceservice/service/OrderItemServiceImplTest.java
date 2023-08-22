package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.BasketItem;
import com.waldron.ecommerceservice.entity.OrderItem;
import com.waldron.ecommerceservice.entity.Product;
import com.waldron.ecommerceservice.exception.NotFoundException;
import com.waldron.ecommerceservice.repository.OrderItemRepository;
import com.waldron.ecommerceservice.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class OrderItemServiceImplTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    @Test
    public void getOrderItemsForOrderId_shouldReturnAFluxOfOrderItems(){

        Long orderId = 1l;

        OrderItem orderItem1 = OrderItem.builder().productId(1l).build();
        OrderItem orderItem2 = OrderItem.builder().productId(1l).build();

        when(orderItemRepository.findByOrderId(orderId)).thenReturn(Flux.just(orderItem1, orderItem2));
        when(productRepository.findById(anyLong())).thenReturn(Mono.just(Product.builder().build()));

        StepVerifier.create(orderItemService.getOrderItemsForOrderId(orderId))
                .expectNext(orderItem1)
                .expectNext(orderItem2)
                .verifyComplete();
    }

    @Test
    public void getOrderItemsForOrderId_shouldReturnNotFoundException_WhenOrderItemDoesNotExist(){

        Long orderId = 1l;

        when(orderItemRepository.findByOrderId(orderId)).thenReturn(Flux.empty());

        StepVerifier.create(orderItemService.getOrderItemsForOrderId(orderId))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    public void getOrderItemsForOrderId_shouldAddCorrectProductToOrderItem(){

        Long orderId = 1l;
        Long product1Id = 1l;
        Long product2Id = 1l;

        OrderItem orderItem1 = OrderItem.builder().orderId(orderId).productId(product1Id).build();
        OrderItem orderItem2 = OrderItem.builder().orderId(orderId).productId(product2Id).build();

        Product product1 = Product.builder().id(product1Id).build();
        OrderItem expectedOrderItem1 = OrderItem.builder().orderId(orderId).productId(product1.getId()).product(product1).build();
        Product product2 = Product.builder().id(product2Id).build();
        OrderItem expectedOrderItem2 = OrderItem.builder().orderId(orderId).productId(product2.getId()).product(product2).build();

        when(orderItemRepository.findByOrderId(orderId)).thenReturn(Flux.just(orderItem1, orderItem2));
        when(productRepository.findById(product1Id)).thenReturn(Mono.just(product1));
        when(productRepository.findById(product2Id)).thenReturn(Mono.just(product2));

        StepVerifier.create(orderItemService.getOrderItemsForOrderId(orderId))
                .expectNext(expectedOrderItem1)
                .expectNext(expectedOrderItem2)
                .verifyComplete();

    }

    @Test
    public void createOrderItem_shouldPassOrderItemToRepository(){

        Long orderItemId = 1l;
        Long productId = 2l;
        Product product = Product.builder()
                .id(productId)
                .name("Book")
                .price(BigDecimal.valueOf(19.99))
                .build();
        OrderItem newOrderItem = OrderItem.builder()
                .id(orderItemId)
                .product(product)
                .productCount(1)
                .build();

        when(orderItemRepository.save(newOrderItem)).thenReturn(Mono.just(newOrderItem));

        StepVerifier.create(orderItemService.createOrderItem(newOrderItem))
                .expectNext(newOrderItem)
                .verifyComplete();
    }

    @Test
    public void mapBasketItemToOrderItem_shouldCopyValuesFromBasketItem(){

        Product product = Product.builder()
                .id(1l)
                .name("Book")
                .price(BigDecimal.valueOf(19.99))
                .build();
        BasketItem basketItem = BasketItem.builder()
                .id(1l)
                .product(product)
                .productCount(1)
                .build();

        OrderItem orderItem = orderItemService.mapBasketItemToOrderItem(basketItem);
        assertEquals(basketItem.getProductId(), orderItem.getProductId());
        assertEquals(basketItem.getProduct(), orderItem.getProduct());
        assertEquals(basketItem.getProductCount(), orderItem.getProductCount());
    }

    @Test
    public void deleteOrderItemForId_shouldPassIdToRepository(){
        Long orderItemId = 1l;

        orderItemService.deleteOrderItemForId(orderItemId);

        ArgumentCaptor<Long> longCaptor = ArgumentCaptor.forClass(Long.class);
        verify(orderItemRepository).deleteById(longCaptor.capture());
        assertEquals(orderItemId, longCaptor.getValue());
    }

    @Test
    public void getTotalPrice_shouldReturnSumOfProductsPrice(){

        Long productId = 2l;
        Product product = Product.builder()
                .id(productId)
                .name("Book")
                .price(BigDecimal.valueOf(10.00))
                .build();
        OrderItem orderItem = OrderItem.builder()
                .product(product)
                .productCount(5)
                .build();

        BigDecimal price = orderItemService.getTotalPrice(orderItem);

        assertEquals(BigDecimal.valueOf(50.00), price);
    }
}