package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.dto.OrderDto;
import com.waldron.ecommerceservice.entity.*;
import com.waldron.ecommerceservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemService orderItemService;

    @Mock
    private BasketService basketService;

    @Mock
    private OrderMapperService orderMapperService;

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
    public void createOrderFromBasket_shouldMapDtoRoEntity(){

        Long basketId = 1l;
        String name = "name";
        String address = "address";
        OrderDto orderDto = new OrderDto(basketId, name, address);

        OrderItem orderItem1 = OrderItem.builder().id(1l).build();
        OrderItem orderItem2 = OrderItem.builder().id(2l).build();
        Set<OrderItem> orderItems = new HashSet<>(Arrays.asList(orderItem1, orderItem2));
        Order newOrder = Order.builder().orderItems(orderItems).build();

        when(orderMapperService.mapDtoToNewEntity(orderDto)).thenReturn(newOrder);
        when(basketService.getBasketForId(basketId)).thenReturn(Mono.just(Basket.builder().build()));
        when(orderRepository.save(any(Order.class))).thenReturn(Mono.just(newOrder));
        when(orderItemService.createOrderItem(any(OrderItem.class))).thenReturn(Mono.just(OrderItem.builder().build()));

        StepVerifier.create(orderService.createOrderFromBasket(orderDto))
                .expectNext(newOrder)
                .verifyComplete();

        ArgumentCaptor<OrderDto> orderDtoCaptor = ArgumentCaptor.forClass(OrderDto.class);
        verify(orderMapperService).mapDtoToNewEntity(orderDtoCaptor.capture());
        assertEquals(orderDto, orderDtoCaptor.getValue());

    }

    @Test
    public void createOrderFromBasket_shouldRequestBasketFromBasketService_whenProvidedABasketId(){

        Long basketId = 1l;
        String name = "name";
        String address = "address";
        OrderDto orderDto = new OrderDto(basketId, name, address);

        OrderItem orderItem1 = OrderItem.builder().id(1l).build();
        OrderItem orderItem2 = OrderItem.builder().id(2l).build();
        Set<OrderItem> orderItems = new HashSet<>(Arrays.asList(orderItem1, orderItem2));
        Order newOrder = Order.builder().orderItems(orderItems).build();

        when(orderMapperService.mapDtoToNewEntity(orderDto)).thenReturn(newOrder);
        when(basketService.getBasketForId(basketId)).thenReturn(Mono.just(Basket.builder().build()));
        when(orderRepository.save(newOrder)).thenReturn(Mono.just(newOrder));
        when(orderItemService.createOrderItem(any(OrderItem.class))).thenReturn(Mono.just(OrderItem.builder().build()));

        orderService.createOrderFromBasket(orderDto);

        ArgumentCaptor<Long> longCaptor = ArgumentCaptor.forClass(Long.class);
        verify(basketService).getBasketForId(longCaptor.capture());
        assertEquals(basketId, longCaptor.getValue());
    }

    @Test
    public void createOrderFromBasket_shouldMergeTheBasketAndOrderEntities(){

        Long basketId = 1l;
        String name = "name";
        String address = "address";
        OrderDto orderDto = new OrderDto(basketId, name, address);

        OrderItem orderItem1 = OrderItem.builder().id(1l).build();
        OrderItem orderItem2 = OrderItem.builder().id(2l).build();
        Set<OrderItem> orderItems = new HashSet<>(Arrays.asList(orderItem1, orderItem2));
        Order newOrder = Order.builder()
                .orderItems(orderItems)
                .name("name")
                .address("address")
                .build();
        Basket basket = Basket.builder()
                .build();

        when(orderMapperService.mapDtoToNewEntity(orderDto)).thenReturn(newOrder);
        when(basketService.getBasketForId(basketId)).thenReturn(Mono.just(basket));
        when(orderRepository.save(newOrder)).thenReturn(Mono.just(newOrder));
        when(orderItemService.createOrderItem(any(OrderItem.class))).thenReturn(Mono.just(OrderItem.builder().build()));

        orderService.createOrderFromBasket(orderDto);

        ArgumentCaptor<Basket> basketCaptor = ArgumentCaptor.forClass(Basket.class);
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderMapperService).mapBasketToOrder(basketCaptor.capture(), orderCaptor.capture());
        assertEquals(basket, basketCaptor.getValue());
        assertEquals(newOrder, orderCaptor.getValue());
    }

    @Test
    public void createOrderFromBasket_shouldSetStatusToPreparing(){

        Long basketId = 1l;
        String name = "name";
        String address = "address";
        OrderDto orderDto = new OrderDto(basketId, name, address);

        OrderItem orderItem1 = OrderItem.builder().id(1l).build();
        OrderItem orderItem2 = OrderItem.builder().id(2l).build();
        Set<OrderItem> orderItems = new HashSet<>(Arrays.asList(orderItem1, orderItem2));
        Order newOrder = Order.builder().orderItems(orderItems).build();
        Basket basket = Basket.builder()
                .build();

        when(orderMapperService.mapDtoToNewEntity(orderDto)).thenReturn(newOrder);
        when(basketService.getBasketForId(basketId)).thenReturn(Mono.just(basket));
        when(orderRepository.save(newOrder)).thenReturn(Mono.just(newOrder));
        when(orderItemService.createOrderItem(any(OrderItem.class))).thenReturn(Mono.just(OrderItem.builder().build()));

        orderService.createOrderFromBasket(orderDto);

        assertEquals(Status.PREPARING, newOrder.getStatus());

    }

    @Test
    public void createOrderFromBasket_shouldSaveTheNewOrder(){

        Long basketId = 1l;
        String name = "name";
        String address = "address";
        OrderDto orderDto = new OrderDto(basketId, name, address);

        OrderItem orderItem1 = OrderItem.builder().id(1l).build();
        OrderItem orderItem2 = OrderItem.builder().id(2l).build();
        Set<OrderItem> orderItems = new HashSet<>(Arrays.asList(orderItem1, orderItem2));
        Order newOrder = Order.builder().orderItems(orderItems).build();
        Basket basket = Basket.builder()
                .build();

        when(orderMapperService.mapDtoToNewEntity(orderDto)).thenReturn(newOrder);
        when(basketService.getBasketForId(basketId)).thenReturn(Mono.just(basket));
        when(orderRepository.save(newOrder)).thenReturn(Mono.just(newOrder));
        when(orderItemService.createOrderItem(any(OrderItem.class))).thenReturn(Mono.just(OrderItem.builder().build()));

        orderService.createOrderFromBasket(orderDto);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(orderCaptor.capture());
        assertEquals(newOrder, orderCaptor.getValue());

    }

    @Test
    public void createOrderFromBasket_shouldSaveOrderItems_whenOrderContainsOrderItems(){

        Long basketId = 1l;
        String name = "name";
        String address = "address";
        OrderDto orderDto = new OrderDto(basketId, name, address);

        OrderItem orderItem1 = OrderItem.builder().id(1l).build();
        OrderItem orderItem2 = OrderItem.builder().id(2l).build();
        Set<OrderItem> orderItems = new HashSet<>(Arrays.asList(orderItem1, orderItem2));

        Order newOrder = Order.builder().orderItems(orderItems).build();
        Basket basket = Basket.builder()
                .build();

        when(orderMapperService.mapDtoToNewEntity(orderDto)).thenReturn(newOrder);
        when(basketService.getBasketForId(basketId)).thenReturn(Mono.just(basket));
        when(orderRepository.save(newOrder)).thenReturn(Mono.just(newOrder));
        when(orderItemService.createOrderItem(any(OrderItem.class))).thenReturn(Mono.just(OrderItem.builder().build()));

        StepVerifier.create(orderService.createOrderFromBasket(orderDto))
                .expectNext(newOrder)
                .verifyComplete();

        verify(orderItemService, times(2)).createOrderItem(any(OrderItem.class));

    }
}