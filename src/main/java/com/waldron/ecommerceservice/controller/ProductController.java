package com.waldron.ecommerceservice.controller;

import com.waldron.ecommerceservice.entity.Product;
import com.waldron.ecommerceservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    ProductService productService;

    // examples https://www.baeldung.com/java-reactive-systems#3-order-service

    @GetMapping
    public Flux<Product> getProducts() {
        //todo add error, success...
        return productService.getProducts();
    }

    //todo GET gerProductById(Long id)

    /**
     * {
     *     "name": "REST Book",
     *     "price": 19.99
     * }
     *
     * @param product
     * @return
     */
    @PostMapping
    public Mono<Product> createProduct(@RequestBody Product product){

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

    //todo POST createProducts(List<Product>)

    //todo PUT updateProduct(Product)

    //todo DELETE deleteProduct(Long id) /products/{id}
    @DeleteMapping("/{id}")
    public Mono<Void> deleteProductById(@PathVariable Long id){
        return productService.deleteProductById(id);
    }
}
