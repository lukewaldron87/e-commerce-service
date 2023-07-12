package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.Basket;
import com.waldron.ecommerceservice.entity.BasketItem;
import com.waldron.ecommerceservice.entity.Product;
import com.waldron.ecommerceservice.repository.BasketRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@SpringBootTest
class BasketServiceImplTest {

    @Mock
    private BasketRepository basketRepository;

    @Mock
    private BasketItemService basketItemService;

    @InjectMocks
    private BasketServiceImpl basketService;

    //todo fix test and refactor getBasketForId to functional solution
    /*@Test
    public void getBasketForId_shouldReturnError_whenBasketNotFound(){

        Long basketId = 1l;

        when(basketRepository.findById(basketId)).thenReturn(Mono.empty());

        StepVerifier.create(basketService.getBasketForId(basketId))
                .expectError(NotFoundException.class)
                .verify();
    }*/

    @Test
    public void getBasketForId_shouldPopulateBasketItems_whenBasketItemsArePresent(){

        Basket expectedBasket = mockSuccessfulGetBasketForId();

        StepVerifier.create(basketService.getBasketForId(expectedBasket.getId()))
                .expectNext(expectedBasket)
                .verifyComplete();

    }

    /*@Test
    public void createBasket_shouldCreateNewBasket_whenProvidedProduct(){
        int numberOfProducts = 1;
        Product product = Product.builder().id(1l).build();

        basketService.createBasket();
    }*/

    @Test
    public void addProductToBasket_shouldAddNewBasketItem_whenProductNotInBasket(){
        int numberOfProducts = 1;
        Product productToAdd = Product.builder().id(3l).build();
        Basket basket = mockSuccessfulGetBasketForId();

        when(basketItemService.createBasketItem(any())).thenReturn(Mono.just(BasketItem.builder().build()));

        Mono<Basket> returnedBasket = basketService.addProductToBasket(basket.getId(),
                                                                       productToAdd.getId(),
                                                                       numberOfProducts);

        StepVerifier.create(returnedBasket)
                .assertNext(basketToVerify -> assertEquals(
                        productToAdd.getId(),
                        basketToVerify.getBasketItemForProductId(productToAdd.getId()).getProductId()
                ))
                .verifyComplete();
    }

    @Test
    public void addProductToBasket_shouldAddNumberOfProductsBasketItem_whenProductInBasket(){
        int numberOfProducts = 1;
        Long productId = 1l;
        Basket basket = mockSuccessfulGetBasketForId();
        Product productToAdd = basket.getBasketItemForProductId(productId).getProduct();

        BasketItem updatedBasketItem = BasketItem.builder().productCount(2).build();
        when(basketItemService.addNumberOfProducts(any(), anyInt())).thenReturn(updatedBasketItem);

        Mono<Basket> returnedBasket = basketService.addProductToBasket(basket.getId(),
                productToAdd.getId(),
                numberOfProducts);

        StepVerifier.create(returnedBasket)
                .assertNext(basket1 -> assertEquals(updatedBasketItem.getProductCount(),
                        basket1.getBasketItemForProductId(productToAdd.getId()).getProductCount())
                )
                .verifyComplete();
    }

    private Basket mockSuccessfulGetBasketForId() {
        Basket repositoryBasket = Basket.builder()
                .id(1l)
                .build();

        Product product1 = Product.builder().id(1l).build();

        BasketItem basketItem1 = BasketItem.builder()
                .id(1l)
                .basketId(repositoryBasket.getId())
                .productId(product1.getId())
                .product(product1)
                .productCount(1)
                .build();

        Product product2 = Product.builder().id(2l).build();

        BasketItem basketItem2 = BasketItem.builder()
                .id(2l)
                .basketId(repositoryBasket.getId())
                .productId(product2.getId())
                .product(product2)
                .productCount(1)
                .build();

        Map<Long, BasketItem> goodIdToBasketItemMap = new HashMap<>();
        goodIdToBasketItemMap.put(basketItem1.getProductId(), basketItem1);
        goodIdToBasketItemMap.put(basketItem2.getProductId(), basketItem2);

        when(basketRepository.findById(repositoryBasket.getId())).thenReturn(Mono.just(repositoryBasket));
        when(basketItemService.getBasketItemsForBasketId(repositoryBasket.getId())).thenReturn(Flux.just(basketItem1, basketItem2));

        Basket expectedBasket = Basket.builder()
                .id(repositoryBasket.getId())
                .goodIdToBasketItemMap(goodIdToBasketItemMap)
                .build();

        return expectedBasket;
    }

}