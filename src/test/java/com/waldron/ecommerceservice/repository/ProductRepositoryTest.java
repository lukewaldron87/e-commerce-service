package com.waldron.ecommerceservice.repository;

import com.waldron.ecommerceservice.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

@DataR2dbcTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void findProductById_shouldGetCorrectProduct_when_productAddedToDatabase() {

        Product product = Product.builder()
                .name("Book 1")
                .price(BigDecimal.valueOf(19.99))
                .build();

        productRepository.save(product).log().subscribe();

        // the original product is updated with an id and this can now be used to retrieve it from the database
        StepVerifier.create(productRepository.findById(product.getId()))
                .expectNextMatches(productCreated -> productCreated.equals(product))
                .verifyComplete();
    }

    @Test
    public void delete_shouldRemoveAProduct_when_passedAnExistingProduct(){

        Product product = Product.builder()
                .name("Book 1")
                .price(BigDecimal.valueOf(19.99))
                .build();

        productRepository.save(product).subscribe();

        productRepository.deleteById(product.getId())
                .as(StepVerifier::create)
                .expectNextCount(0)
                .verifyComplete();
    }
}