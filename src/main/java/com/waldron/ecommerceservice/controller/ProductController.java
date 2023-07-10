package com.waldron.ecommerceservice.controller;

import com.waldron.ecommerceservice.entity.Product;
import com.waldron.ecommerceservice.service.ProductService;
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
        //todo add error, success...
        return productService.getProducts();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    //todo add validation with @Valid
    public Mono<Product> createProduct(@RequestBody Product product){

        //todo validate product entity

        return productService.createProduct(product);

        //todo failure response
        /*return orderService.createOrder(order)
                .flatMap(o -> {
                    if (OrderStatus.FAILURE.equals(o.getOrderStatus())) {
                        return Mono.error(new RuntimeException("Order processing failed, please try again later. " + o.getResponseMessage()));
                    } else {
                        return Mono.just(o);
                    }
                });*/
    }

    @PutMapping("/{id}")
    public Mono<Product> updateProductForId(@PathVariable Long id, @RequestBody Product product){
        return productService.updateProductForId(id, product);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteProductForId(@PathVariable Long id){
        return productService.deleteProductForId(id);
    }
}
