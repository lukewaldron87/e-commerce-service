package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.BasketItem;
import com.waldron.ecommerceservice.entity.Product;
import com.waldron.ecommerceservice.exception.NotFoundException;
import com.waldron.ecommerceservice.repository.BasketItemRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class BasketItemServiceImplTest {

    @Mock
    private BasketItemRepository basketItemRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private BasketItemServiceImpl basketItemService;

    @Test
    public void getBasketItemForId_shouldReturnError_whenBasketItemDoesNotExist(){

        Long basketItemId = 1l;
        when(basketItemRepository.findById(basketItemId)).thenReturn(Mono.empty());

        StepVerifier.create(basketItemService.getBasketItemForId(basketItemId))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    public void getBasketItemForId_shouldAddCorrectProductToBasketItem_whenProductExists(){

        Long basketItemId = 1l;
        Long productId = 2l;
        BasketItem basketItemFromRepository = BasketItem.builder()
                .id(basketItemId)
                .productId(productId)
                .productCount(1)
                .build();

        Product product = Product.builder()
                .id(productId)
                .name("Book")
                .price(BigDecimal.valueOf(19.99))
                .build();

        BasketItem expectedBasketItem = BasketItem.builder()
                .id(basketItemId)
                .productId(productId)
                .productCount(basketItemFromRepository.getProductCount())
                .product(product)
                .build();

        when(basketItemRepository.findById(basketItemId)).thenReturn(Mono.just(basketItemFromRepository));
        when(productRepository.findById(productId)).thenReturn(Mono.just(product));

        StepVerifier.create(basketItemService.getBasketItemForId(basketItemId))
                .expectNext(expectedBasketItem)
                .verifyComplete();
    }

    @Test
    public void getBasketItemForId_shouldReturnNotFoundException_whenProductDoesNotExist(){

        Long basketItemId = 1l;
        Long productId = 2l;
        BasketItem basketItemFromRepository = BasketItem.builder()
                .id(basketItemId)
                .productId(productId)
                .productCount(1)
                .build();

        when(basketItemRepository.findById(basketItemId)).thenReturn(Mono.just(basketItemFromRepository));
        when(productRepository.findById(productId)).thenReturn(Mono.empty());

        StepVerifier.create(basketItemService.getBasketItemForId(basketItemId))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    public void getBasketItemsForBasketId_shouldAddCorrectProductToBasketItem(){

        Long basketId = 1l;
        Long productId = 2l;
        BasketItem basketItem1 = BasketItem.builder().basketId(basketId).productId(productId).build();
        BasketItem basketItem2 = BasketItem.builder().basketId(basketId).productId(productId).build();

        Product product = Product.builder().id(productId).build();
        BasketItem expectedBasketItem1 = BasketItem.builder().basketId(basketItem1.getBasketId()).productId(basketItem1.getProductId())
                .product(product).build();
        BasketItem expectedBasketItem2 = BasketItem.builder().basketId(basketItem2.getBasketId()).productId(basketItem2.getProductId())
                .product(product).build();

        when(basketItemRepository.findByBasketId(basketId)).thenReturn(Flux.just(basketItem1, basketItem2));
        when(productRepository.findById(productId)).thenReturn(Mono.just(product));

        StepVerifier.create(basketItemService.getBasketItemsForBasketId(basketId))
                .expectNext(expectedBasketItem1)
                .expectNext(expectedBasketItem2)
                .verifyComplete();
    }

    @Test
    public void createBasketItem_shouldPassBasketItemToRepository(){

        Long basketItemId = 1l;
        Long productId = 2l;
        Product product = Product.builder()
                .id(productId)
                .name("Book")
                .price(BigDecimal.valueOf(19.99))
                .build();
        BasketItem newBasketItem = BasketItem.builder()
                .id(basketItemId)
                .product(product)
                .productCount(1)
                .build();

        when(basketItemRepository.save(newBasketItem)).thenReturn(Mono.just(newBasketItem));

        StepVerifier.create(basketItemService.createBasketItem(newBasketItem))
                .expectNext(newBasketItem)
                .verifyComplete();
    }

    @Test
    public void createBasketItem_shouldAddProductIdToBasketItem_whenProductIdNotProvided(){

        Long basketItemId = 1l;
        Long productId = 2l;
        Product product = Product.builder()
                .id(productId)
                .name("Book")
                .price(BigDecimal.valueOf(19.99))
                .build();
        BasketItem newBasketItem = BasketItem.builder()
                .id(basketItemId)
                .product(product)
                .productCount(1)
                .build();

        BasketItem expectedBasketItem = BasketItem.builder()
                .id(basketItemId)
                .product(product)
                .productId(productId)
                .productCount(newBasketItem.getProductCount())
                .build();

        when(basketItemRepository.save(newBasketItem)).thenReturn(Mono.just(expectedBasketItem));

        StepVerifier.create(basketItemService.createBasketItem(newBasketItem))
                .expectNext(expectedBasketItem)
                .verifyComplete();

        ArgumentCaptor<BasketItem> argumentCaptor = ArgumentCaptor.forClass(BasketItem.class);
        verify(basketItemRepository).save(argumentCaptor.capture());
        assertEquals(productId, argumentCaptor.getValue().getProductId());
    }

    @Test
    public void createBasketItem_shouldSetProductCountToOne_whenProductCountIsNotSet() {

        Long basketItemId = 1l;
        Long productId = 2l;
        BasketItem newBasketItem = BasketItem.builder()
                .id(basketItemId)
                .productId(productId)
                .build();

        int expectedProductCount = 1;
        BasketItem expectedBasketItem = BasketItem.builder()
                .id(basketItemId)
                .productId(productId)
                .productCount(expectedProductCount)
                .build();

        when(basketItemRepository.save(newBasketItem)).thenReturn(Mono.just(expectedBasketItem));

        StepVerifier.create(basketItemService.createBasketItem(newBasketItem))
                .expectNext(expectedBasketItem)
                .verifyComplete();

        ArgumentCaptor<BasketItem> argumentCaptor = ArgumentCaptor.forClass(BasketItem.class);
        verify(basketItemRepository).save(argumentCaptor.capture());
        assertEquals(expectedProductCount, argumentCaptor.getValue().getProductCount());
    }

    @Test
    public void updatedBasketItem_shouldPassBasketItemToRepository(){

        Long basketItemId = 1l;
        Long productId = 2l;
        Product product = Product.builder()
                .id(productId)
                .name("Book")
                .price(BigDecimal.valueOf(19.99))
                .build();
        BasketItem updatedBasketItem = BasketItem.builder()
                .id(basketItemId)
                .product(product)
                .productCount(1)
                .build();

        when(basketItemRepository.save(updatedBasketItem)).thenReturn(Mono.just(updatedBasketItem));

        basketItemService.updatedBasketItem(updatedBasketItem);

        ArgumentCaptor<BasketItem> argumentCaptor = ArgumentCaptor.forClass(BasketItem.class);
        verify(basketItemRepository).save(argumentCaptor.capture());
        assertEquals(updatedBasketItem, argumentCaptor.getValue());
    }

    @Test
    public void addNumberOfProducts_shouldAddGivenNumberOfProducts(){
        BasketItem basketItem = BasketItem.builder()
                .id(1l)
                .productCount(1).build();

        when(basketItemRepository.save(basketItem)).thenReturn(Mono.just(basketItem));

        Mono<BasketItem> basketItemMono = basketItemService.addNumberOfProducts(basketItem, 1);

        StepVerifier.create(basketItemMono)
                .assertNext(returnedBasketItem -> assertEquals(2, returnedBasketItem.getProductCount()))
                .verifyComplete();

    }

    @Test
    public void reduceNumberOfProducts_shouldRemoveGivenNumberOfProducts(){
        BasketItem basketItem = BasketItem.builder()
                .id(1l)
                .productCount(3).build();

        when(basketItemRepository.save(basketItem)).thenReturn(Mono.just(basketItem));

        Mono<BasketItem> BasketItemMono = basketItemService.reduceNumberOfProducts(basketItem, 2);
        StepVerifier.create(BasketItemMono)
                .assertNext(returnedBasketItem ->  assertEquals(1, returnedBasketItem.getProductCount()))
                .verifyComplete();
    }

    @Test
    public void reduceNumberOfProducts_shouldUpdateTheBasketItem_whenProductCountDecremented(){

        BasketItem basketItem = BasketItem.builder()
                .id(1l)
                .productCount(3).build();

        when(basketItemRepository.save(basketItem)).thenReturn(Mono.just(basketItem));

        Mono<BasketItem> BasketItemMono = basketItemService.reduceNumberOfProducts(basketItem, 2);
        StepVerifier.create(BasketItemMono)
                .expectNext(basketItem)
                .verifyComplete();

        verify(basketItemRepository, times(1)).save(basketItem);
    }

    @Test
    public void deleteBasketItemForId_shouldPassIdToRepository(){

        Long basketItemId = 1l;

        basketItemService.deleteBasketItemForId(basketItemId);

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(basketItemRepository).deleteById(argumentCaptor.capture());
        assertEquals(basketItemId, argumentCaptor.getValue());
    }

    @Test
    public void deleteBasketItemsForBasketId_shouldPassBasketIdToRepository(){
        Long basketId = 1l;

        basketItemService.deleteBasketItemsForBasketId(basketId);

        ArgumentCaptor<Long> longCaptor = ArgumentCaptor.forClass(Long.class);
        verify(basketItemRepository).deleteByBasketId(longCaptor.capture());
        assertEquals(basketId, longCaptor.getValue());
    }

    @Test
    public void getTotalPrice_shouldReturnSumOfProductsPrice(){

        Long basketItemId = 1l;
        Long productId = 2l;
        Product product = Product.builder()
                .id(productId)
                .name("Book")
                .price(BigDecimal.valueOf(10.00))
                .build();
        BasketItem basketItem = BasketItem.builder()
                .id(basketItemId)
                .product(product)
                .productCount(5)
                .build();

        BigDecimal price = basketItemService.getTotalPrice(basketItem);

        assertEquals(BigDecimal.valueOf(50.00), price);
    }
}