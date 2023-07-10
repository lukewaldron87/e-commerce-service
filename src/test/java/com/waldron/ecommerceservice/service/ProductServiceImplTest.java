package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.Product;
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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    public void getProducts_should_returnAllExpectedProducts(){

        // example: https://medium.com/@BPandey/writing-unit-test-in-reactive-spring-boot-application-32b8878e2f57

        Product product1 = Product.builder()
                .id(1L)
                .name("Book 1")
                .price(BigDecimal.valueOf(19.99))
                .build();
        Product product2 = Product.builder()
                .id(1L)
                .name("Book 1")
                .price(BigDecimal.valueOf(19.99))
                .build();

        when(productRepository.findAll()).thenReturn(Flux.just(product1, product2));

        Flux<Product> returnedProductFlux = productService.getProducts();

        StepVerifier.create(returnedProductFlux)
                .expectNext(product1)
                .expectNext(product2)
                .verifyComplete();
    }

    @Test
    public void createProduct_should_passNewProductToRepository(){
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
    public void createProduct_should_returnCreatedProduct(){
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
    public void deleteProductById_should_passProductToRepository_whenProductExists(){
        Long productId = 1L;

        Product product = Product.builder()
                .id(1L)
                .name("Book 1")
                .price(BigDecimal.valueOf(19.99))
                .build();

        Mono<Product> productMono = Mono.just(product);

        when((productRepository.findById(productId))).thenReturn(productMono);

        productService.deleteProductById(productId);

        //todo this is finding the interaction with productRepository.findById not productRepository::delete
        ArgumentCaptor<Product> argumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).delete(argumentCaptor.capture());
        assertEquals(product, argumentCaptor.getValue());
    }

    //todo add test for product exists

    //todo add test for return Mono<Void>
}