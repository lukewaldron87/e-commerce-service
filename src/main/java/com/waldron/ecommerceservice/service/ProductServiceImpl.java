package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.Product;
import com.waldron.ecommerceservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    ProductRepository productRepository;

    @Override
    public Flux<Product> getProducts() {
        return productRepository.findAll();
    }

    @Override
    public Mono<Product> createProduct(Product product) {
        return productRepository.save(product);
    }

    //todo updateProduct

    //todo deleteProductById
}
