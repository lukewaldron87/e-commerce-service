package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.Product;
import com.waldron.ecommerceservice.exception.NotFoundException;
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

    /**
     * Update product if it already exists
     *
     * @param productId the ID of the product to update
     * @param product the product with the updated fields
     * @return the updated product
     */
    @Override
    public Mono<Product> updateProductForId(Long productId, Product product) {

        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(new NotFoundException("Product not found")))
                // todo add mapping method for update
                .map(foundProduct -> {
                    foundProduct.setName(product.getName());
                    foundProduct.setPrice(product.getPrice());
                    return foundProduct;
                })
                .flatMap(updatedProduct -> productRepository.save(updatedProduct));
    }

    @Override
    public Mono<Void> deleteProductForId(Long productId) {
        return productRepository.deleteById(productId);
        //todo create response for not found
                //.switchIfEmpty(Mono.error(new NotFoundException("Product not found")))
    }
}
