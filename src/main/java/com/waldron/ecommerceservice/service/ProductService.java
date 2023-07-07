package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.Product;
import reactor.core.publisher.Flux;

public interface ProductService {

    public Flux<Product> getProducts();
}
