package com.waldron.ecommerceservice.repository;

import com.waldron.ecommerceservice.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.math.BigInteger;

@DataR2dbcTest
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Test
    public void findProductById_should_getCorrectProduct_when_productAddedToDatabase() {

        Product product = Product.builder()
                .name("Book 1")
                .price(BigDecimal.valueOf(19.99))
                .build();

        productRepository.save(product).subscribe();

        StepVerifier.create(productRepository.findById(product.getId()))
                .expectNextMatches(productCreated -> productCreated.equals(product))
                .verifyComplete();
    }

    //todo add test for delete
    /*@Test
    public void whenDeleteAll_then0IsExpected() {
        playerRepository.deleteAll()
                .as(StepVerifier::create)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void whenInsert6_then6AreExpected() {
        insertPlayers();
        playerRepository.findAll()
                .as(StepVerifier::create)
                .expectNextCount(6)
                .verifyComplete();
    }*/

}