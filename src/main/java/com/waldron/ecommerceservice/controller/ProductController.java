package com.waldron.ecommerceservice.controller;

import com.waldron.ecommerceservice.entity.Product;
import com.waldron.ecommerceservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

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

}
