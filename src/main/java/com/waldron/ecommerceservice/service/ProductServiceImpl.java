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
    private ProductRepository productRepository;

    @Override
    public Flux<Product> getProducts() {
        return productRepository.findAll();
    }

    private static String PRODUCT_NOT_FOUND_MESSAGE = "Product not found";

    @Override
    public Mono<Product> getProductForId(Long id) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(PRODUCT_NOT_FOUND_MESSAGE)));
    }

    @Override
    public Mono<Product> createProduct(Mono<Product> productMono) {
        return productMono.flatMap(product -> productRepository.save(product));
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
                .switchIfEmpty(Mono.error(new NotFoundException(PRODUCT_NOT_FOUND_MESSAGE)))
                // todo add mapping method for update
                .map(foundProduct -> {
                    foundProduct.setName(product.getName());
                    foundProduct.setPrice(product.getPrice());
                    return foundProduct;
                })
                .flatMap(updatedProduct -> productRepository.save(updatedProduct));
    }

    /**
     * Currently delete only works for products that are not associated with a basket or order
     *
     * @param productId
     * @return
     */
    @Override
    public Mono<Void> deleteProductForId(Long productId) {
        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(new NotFoundException(PRODUCT_NOT_FOUND_MESSAGE)))
                .map(Product::getId)
                .flatMap(foundId -> productRepository.deleteById(foundId));
    }
}
