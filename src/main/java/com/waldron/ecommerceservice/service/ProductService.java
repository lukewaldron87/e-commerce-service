package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductService {

    Flux<Product> getProducts();

    Mono<Product> createProduct(Product product);

    Mono<Product> deleteProductById(Long productId);
}
