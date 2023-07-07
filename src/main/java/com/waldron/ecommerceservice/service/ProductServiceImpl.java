package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.Product;
import com.waldron.ecommerceservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    ProductRepository productRepository;

    @Override
    public Flux<Product> getProducts() {
        return productRepository.findAll();
    }
}
