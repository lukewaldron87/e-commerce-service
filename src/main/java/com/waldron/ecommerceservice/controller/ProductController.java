package com.waldron.ecommerceservice.controller;

import com.waldron.ecommerceservice.entity.Product;
import com.waldron.ecommerceservice.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping
    public Flux<Product> getProducts() {
        return productService.getProducts();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Product> createProduct(@Valid @RequestBody Product product){
        return productService.createProduct(product);
    }

    //todo change to  Mono<ResponseEntity<Product>> https://github.com/G-khan/spring-webflux-reactive-rest-api-demo/blob/main/src/main/java/dev/gokhana/reactiveapi/controller/UserController.java
    @PutMapping("/{id}")
    public Mono<Product> updateProductForId(@PathVariable Long id, @Valid @RequestBody Product product){
        return productService.updateProductForId(id, product);
    }

    @DeleteMapping("/{id}")
    //todo test this
    //@ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteProductForId(@PathVariable Long id){
        return productService.deleteProductForId(id);
            //todo add reply for id not found with ResponseEntity
                //.defaultIfEmpty(Mono.new ResponseEntity<>(HttpStatus.NOT_FOUND));
                //.switchIfEmpty(Mono.error(new RuntimeException ("Product not found for id "+id)));
    }
}
