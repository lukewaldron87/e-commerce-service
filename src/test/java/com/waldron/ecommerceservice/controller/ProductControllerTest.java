package com.waldron.ecommerceservice.controller;

import com.waldron.ecommerceservice.entity.Product;
import com.waldron.ecommerceservice.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@WebFluxTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private ProductService productService;

    private static String PRODUCTS_URI = "/products";

    @Test
    public void getProducts_should_getProductsFromService(){

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

        when(productService.getProducts()).thenReturn(Flux.just(product1, product2));

        webClient.get().uri(PRODUCTS_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Product.class)
                .contains(product1, product2);

        Mockito.verify(productService, times(1)).getProducts();
    }

    @Test
    public void createProduct_should_passNewProductToService(){

        Product product = Product.builder()
                .name("Book 1")
                .price(BigDecimal.valueOf(19.99))
                .build();

        webClient.post()
                .uri(PRODUCTS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(product), Product.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Product.class);

        ArgumentCaptor<Product> argumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productService).createProduct(argumentCaptor.capture());
        assertEquals(product, argumentCaptor.getValue());
    }

    @Test
    public void createProduct_should_returnCreatedProduct(){

        Product product = Product.builder()
                .name("Book 1")
                .price(BigDecimal.valueOf(19.99))
                .build();

        Product expectedProduct = product;
        expectedProduct.setId(1L);

        when(productService.createProduct(product)).thenReturn(Mono.just(expectedProduct));

        webClient.post()
                .uri(PRODUCTS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(product), Product.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Product.class)
                .isEqualTo(expectedProduct);

    }

    @Test
    public void deleteProductById_should_passProductIdToService(){

        Long productId = 1L;

        webClient.delete()
                .uri(PRODUCTS_URI+"/"+productId.toString())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class);
                //todo add response to match this
                //.isEqualTo("Product with id 1 is deleted.");

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(productService).deleteProductById(argumentCaptor.capture());
        assertEquals(productId, argumentCaptor.getValue());
    }

    //todo add test for return Mono<Void>
}