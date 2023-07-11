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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    public void getBasketItemForId_shouldReturnBasketItemWithoutProduct_whenProductIdNotProvided(){

        Long basketItemId = 1l;
        BasketItem basketItem = BasketItem.builder()
                .id(basketItemId)
                .productId(null)
                .productCount(1)
                .build();

        when(basketItemRepository.findById(basketItemId)).thenReturn(Mono.just(basketItem));

        StepVerifier.create(basketItemService.getBasketItemForId(basketItemId))
                .expectNext(basketItem)
                .verifyComplete();
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

        Product productUpdate = Product.builder()
                .id(productId)
                .name("Book")
                .price(BigDecimal.valueOf(19.99))
                .build();

        BasketItem expectedBasketItem = BasketItem.builder()
                .id(basketItemId)
                .productId(productId)
                .productCount(basketItemFromRepository.getProductCount())
                .product(productUpdate)
                .build();

        when(basketItemRepository.findById(basketItemId)).thenReturn(Mono.just(basketItemFromRepository));
        when(productRepository.findById(productId)).thenReturn(Mono.just(productUpdate));

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
    public void createBasketItem_shouldPassNewBasketItemToRepository(){

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

}