package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.BasketItem;
import com.waldron.ecommerceservice.entity.Product;
import com.waldron.ecommerceservice.exception.NotFoundException;
import com.waldron.ecommerceservice.repository.BasketItemRepository;
import com.waldron.ecommerceservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

public class BasketItemServiceImpl implements BasketItemService {

    @Autowired
    private BasketItemRepository basketItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Mono<BasketItem> getBasketItemForId(Long basketItemId) {
        return basketItemRepository.findById(basketItemId)
                .switchIfEmpty(Mono.error(new NotFoundException("Basket Item not found")))
                .flatMap(basketItem -> {
                    if (basketItem.getProductId() == null) {
                        return Mono.just(basketItem);
                    }
                    // interacting directly with repository instead of service as no business logic required when fetching product by ID
                    return productRepository.findById(basketItem.getProductId())
                            .switchIfEmpty(Mono.error(new NotFoundException("Product not found")))
                            .map(product ->
                            {
                                basketItem.setProduct(product);
                                return basketItem;
                            });
                });
    }

    @Override
    public Mono<BasketItem> createBasketItem(BasketItem basketItem) {
        //todo verify BasketItem has product or productId
        verifyProductId(basketItem);
        verifyProductCount(basketItem);

        return basketItemRepository.save(basketItem);
    }

    @Override
    public Mono<BasketItem> updatedBasketItem(BasketItem updatedBasketItem) {

        //todo add BasketItem mapping

        return basketItemRepository.save(updatedBasketItem);
    }

    private static void verifyProductId(BasketItem basketItem) {
        //todo throw exception if Product missing
        if (basketItem.getProductId() == null) {
            basketItem.setProductId(basketItem.getProduct().getId());
        }
    }

    private static void verifyProductCount(BasketItem basketItem) {
        if (basketItem.getProductCount() == 0) {
            basketItem.setProductCount(1);
        }
    }

    // todo add x number of product or should it be an update method?
    //or add x number of product and update
    // or does this go in the entity???

    @Override
    public Mono<Void> deleteBasketItemForId(Long basketItemId) {
        return basketItemRepository.deleteById(basketItemId);
    }

    // todo add getTotalPrice or does this go in the entity???
}
