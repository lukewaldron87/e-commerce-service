package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.Basket;
import com.waldron.ecommerceservice.entity.Order;
import com.waldron.ecommerceservice.entity.OrderItem;
import com.waldron.ecommerceservice.entity.Product;
import com.waldron.ecommerceservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemService orderItemService;

    @Mock
    private BasketService basketService;

    @InjectMocks
    private OrderServiceImpl orderService;

    //todo fix tests
    /*@Test
    public void getOrderForId_shouldGetOrderForGivenId(){

        Long orderId = 1l;
        Order expectedOrder = Order.builder().id(orderId).build();

        when(orderRepository.findById(orderId)).thenReturn(Mono.just(expectedOrder));

        StepVerifier.create(orderService.getOrderForId(orderId))
                .expectNext(expectedOrder)
                .verifyComplete();
    }

    @Test
    public void getOrderForId_shouldReturnErrorIfOrderNotFound(){
        Long orderId = 1l;

        when(orderRepository.findById(orderId)).thenReturn(Mono.empty());

        StepVerifier.create(orderService.getOrderForId(orderId))
                .expectError(NotFoundException.class)
                .verify();
    }*/

    @Test void getOrderForId_shouldAddOrderItemsToOrder(){

        Long orderId = 1l;
        Order expectedOrder = mockSuccessfullyGetOrder(orderId);

        StepVerifier.create(orderService.getOrderForId(orderId))
                .expectNext(expectedOrder)
                .verifyComplete();

    }

    @Test
    public void getTotalPriceForOrderId_shouldReturnTheSumOfAllProducts(){

        Long orderId = 1l;
        mockSuccessfullyGetOrder(orderId);

        when(orderItemService.getTotalPrice(any())).thenReturn(BigDecimal.valueOf(100.00));

        Mono<BigDecimal> totalPrice = orderService.getTotalPriceForOrderId(orderId);

        StepVerifier.create(totalPrice)
                .assertNext(totalPriceToAssert ->
                        assertEquals(BigDecimal.valueOf(200.00), totalPriceToAssert)
                )
                .verifyComplete();
    }

    private Order mockSuccessfullyGetOrder(Long orderId) {
        Order order = Order.builder().id(orderId).build();

        Product product1 = Product.builder().id(1l).build();

        OrderItem orderItem1 = OrderItem.builder()
                .orderId(orderId)
                .productId(product1.getId())
                .productCount(1)
                .product(product1)
                .build();


        Product product2 = Product.builder().id(2l).build();

        OrderItem orderItem2 = OrderItem.builder()
                .orderId(orderId)
                .productId(product2.getId())
                .productCount(1)
                .product(product2)
                .build();

        Set<OrderItem> orderItemSet = new HashSet<>();
        orderItemSet.add(orderItem1);
        orderItemSet.add(orderItem2);

        Order expectedOrder = Order.builder().id(orderId).orderItems(orderItemSet).build();

        when(orderRepository.findById(orderId)).thenReturn(Mono.just(order));
        when(orderItemService.getOrderItemsForOrderId(orderId)).thenReturn(Flux.just(orderItem1, orderItem2));
        return expectedOrder;
    }

    @Test
    public void createOrderFromBasket_shouldRequestBasketFromBasketService_whenProvidedABasketId(){

        Order newOrder = Order.builder().build();
        Long basketId = 1l;

        when(basketService.getBasketForId(basketId)).thenReturn(Mono.just(Basket.builder().build()));

        orderService.createOrderFromBasket(newOrder, basketId);

        ArgumentCaptor<Long> longCaptor = ArgumentCaptor.forClass(Long.class);
        verify(basketService).getBasketForId(longCaptor.capture());
        assertEquals(basketId, longCaptor.getValue());
    }

    @Test
    public void createOrderFromBasket_shouldMergeTheBasketAndOrderEntities(){

        Order newOrder = Order.builder()
                .name("name")
                .address("address")
                .build();
        Long basketId = 1l;
        Basket basket = Basket.builder()
                .build();

        when(basketService.getBasketForId(basketId)).thenReturn(Mono.just(Basket.builder().build()));

    }
}