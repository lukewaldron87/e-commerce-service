package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.Product;
import com.waldron.ecommerceservice.exception.NotFoundException;
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
import static org.mockito.Mockito.*;

@SpringBootTest
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    public void getProducts_shouldReturnAllExpectedProducts(){

        Product product1 = Product.builder()
                .id(1L)
                .name("Book 1")
                .price(BigDecimal.valueOf(19.99))
                .build();
        Product product2 = Product.builder()
                .id(2L)
                .name("Book 2")
                .price(BigDecimal.valueOf(29.99))
                .build();

        when(productRepository.findAll()).thenReturn(Flux.just(product1, product2));

        Flux<Product> returnedProductFlux = productService.getProducts();

        StepVerifier.create(returnedProductFlux)
                .expectNext(product1)
                .expectNext(product2)
                .verifyComplete();
    }

    @Test
    public void createProduct_shouldPassNewProductToRepository(){
        Product product = Product.builder()
                .name("Book 1")
                .price(BigDecimal.valueOf(19.99))
                .build();

        productService.createProduct(product);

        ArgumentCaptor<Product> argumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(argumentCaptor.capture());

        assertEquals(product, argumentCaptor.getValue());
    }

    @Test
    public void createProduct_shouldReturnCreatedProduct(){
        Product newProduct = Product.builder()
                .name("Book 1")
                .price(BigDecimal.valueOf(19.99))
                .build();

        when(productRepository.save(newProduct)).thenReturn(Mono.just(newProduct));

        Mono<Product> returnedProduct = productService.createProduct(newProduct);

        StepVerifier.create(returnedProduct)
                .expectNext(newProduct)
                .verifyComplete();

    }

    @Test
    public void updateProductForId_shouldPassProductToRepository_whenProductExists(){
        Long productId = 1L;
        Product existingProduct = Product.builder()
                .id(productId)
                .name("Book 1")
                .price(BigDecimal.valueOf(19.99))
                .build();
        Product productUpdate = Product.builder()
                .id(null)
                .name("Book Update")
                .price(BigDecimal.valueOf(99.99))
                .build();

        when(productRepository.findById(productId)).thenReturn(Mono.just(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(productUpdate));

        StepVerifier.create(productService.updateProductForId(productId, productUpdate))
                .expectNext(productUpdate)
                .verifyComplete();

        ArgumentCaptor<Product> argumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(argumentCaptor.capture());
        assertEquals(existingProduct.getId(), argumentCaptor.getValue().getId());
        assertEquals(productUpdate.getName(), argumentCaptor.getValue().getName());
        assertEquals(productUpdate.getPrice(), argumentCaptor.getValue().getPrice());

    }

    @Test
    public void updateProductForId_returnMonoError_whenProductDoesNotExits() {

        Long productId = 1L;
        Product productUpdate = Product.builder()
                .id(null)
                .name("Book Update")
                .price(BigDecimal.valueOf(99.99))
                .build();

        when(productRepository.findById(productId)).thenReturn(Mono.empty());

        StepVerifier.create(productService.updateProductForId(productId, productUpdate))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    public void deleteProductForId_shouldPassProductIfToRepository(){

        Long productId = 1L;

        productService.deleteProductForId(productId);

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(productRepository).deleteById(argumentCaptor.capture());
        assertEquals(productId, argumentCaptor.getValue());
    }

    //todo add test for return Mono<Void>
}