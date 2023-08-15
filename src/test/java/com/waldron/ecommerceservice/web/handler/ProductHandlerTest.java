package com.waldron.ecommerceservice.web.handler;

import com.waldron.ecommerceservice.config.ProductRouter;
import com.waldron.ecommerceservice.entity.Product;
import com.waldron.ecommerceservice.exception.NotFoundException;
import com.waldron.ecommerceservice.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductHandlerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private ProductService productService;

    @Test
    public void getProducts_shouldGetProductsFromService(){

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

        when(productService.getProducts()).thenReturn(Flux.just(product1, product2));

        webClient.get().uri(ProductRouter.PRODUCTS_URL)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Product.class)
                .contains(product1, product2);

        Mockito.verify(productService, times(1)).getProducts();
    }

    //todo fix
    /*@Test
    public void createProduct_shouldPassNewProductToService(){

        Product product = Product.builder()
                .name("Book 1")
                .price(BigDecimal.valueOf(19.99))
                .build();

        Product expectedProduct = product;
        expectedProduct.setId(1L);

        when(productService.createProduct(any(Mono.class))).thenReturn(Mono.just(expectedProduct));

        webClient.post()
                .uri(ProductRouter.PRODUCTS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(product), Product.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Product.class);



        ArgumentCaptor<Mono<Product>> argumentCaptor = ArgumentCaptor.forClass(Mono.class);
        verify(productService).createProduct(argumentCaptor.capture());
        assertEquals(product, argumentCaptor.getValue());
    }*/

    @Test
    public void createProduct_shouldReturnCreatedProduct(){

        Product product = Product.builder()
                .name("Book 1")
                .price(BigDecimal.valueOf(19.99))
                .build();

        Product expectedProduct = product;
        expectedProduct.setId(1L);

        when(productService.createProduct(any(Mono.class))).thenReturn(Mono.just(expectedProduct));

        webClient.post()
                .uri(ProductRouter.PRODUCTS_URL)
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
    public void updateProductForId_shouldPassProductToService(){
        Long productId = 1L;
        Product product = Product.builder()
                .id(productId)
                .name("Book 1")
                .price(BigDecimal.valueOf(19.99))
                .build();

        when(productService.updateProductForId(productId, product)).thenReturn(Mono.just(product));

        webClient.put()
                .uri(ProductRouter.PRODUCTS_URL+"/"+productId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(product), Product.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Product.class)
                .isEqualTo(product);

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productService).updateProductForId(idCaptor.capture(), productCaptor.capture());
        assertEquals(productId, idCaptor.getValue());
        assertEquals(product, productCaptor.getValue());
    }

    @Test
    public void updateProductForId_shouldReturnNotFound_whenEmptyMonoReturnedFrom() {
        Long productId = 1L;
        Product product = Product.builder()
                .id(productId)
                .name("Book 1")
                .price(BigDecimal.valueOf(19.99))
                .build();

        when(productService.updateProductForId(productId, product)).thenReturn(Mono.empty());

        webClient.put()
                .uri(ProductRouter.PRODUCTS_URL + "/" + productId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(product), Product.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void deleteProductForId_shouldPassProductIdToService(){

        Long productId = 1L;

        when(productService.deleteProductForId(productId)).thenReturn(Mono.empty());

        webClient.delete()
                .uri(ProductRouter.PRODUCTS_URL+"/"+productId.toString())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class);
        //todo add response to match this
        //.isEqualTo("Product with id 1 is deleted.");

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(productService).deleteProductForId(argumentCaptor.capture());
        assertEquals(productId, argumentCaptor.getValue());
    }

    @Test
    public void deleteProductForId_shouldReturnNotFound_when() {

        Long productId = 1L;

        when(productService.deleteProductForId(productId)).thenThrow(new NotFoundException("not found"));

        webClient.delete()
                .uri(ProductRouter.PRODUCTS_URL + "/" + productId.toString())
                .exchange()
                .expectStatus().isNotFound();
    }
}