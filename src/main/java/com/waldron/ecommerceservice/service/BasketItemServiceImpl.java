package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.BasketItem;
import com.waldron.ecommerceservice.exception.NotFoundException;
import com.waldron.ecommerceservice.repository.BasketItemRepository;
import com.waldron.ecommerceservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
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
                    return addProductToBasketItem(basketItem);
                });
    }

    /**
     * Returns all basket items associated with the given basket
     *
     * poor loose coupling but needed to allow joining entities/tables and maintain non-blocking state
     *
     * @param basketId the id of the basket
     * @return all basket items associated with the given basket
     */
    @Override
    public Flux<BasketItem> getBasketItemsForBasketId(Long basketId) {
        return basketItemRepository.findByBasketId(basketId)
                .flatMap(basketItem -> {
                    if (basketItem.getProductId() == null) {
                        return Mono.just(basketItem);
                    }
                    return addProductToBasketItem(basketItem);
                });
    }

    //todo change to getProduct in service
    private Mono<BasketItem> addProductToBasketItem(BasketItem basketItem) {
        // interacting directly with repository instead of service as no business logic required when fetching product by ID
        return productRepository.findById(basketItem.getProductId())
                .switchIfEmpty(Mono.error(new NotFoundException("Product not found")))
                .map(product ->
                {
                    basketItem.setProduct(product);
                    return basketItem;
                });
    }

    @Override
    public Mono<BasketItem> createBasketItem(BasketItem basketItem) {
        //todo refactor to functional/reactive solution
        verifyProductId(basketItem);
        verifyProductCount(basketItem);

        return basketItemRepository.save(basketItem);
    }

    private static void verifyProductId(BasketItem basketItem) {
        if (basketItem.getProductId() == null) {
            basketItem.setProductId(basketItem.getProduct().getId());
        }
    }

    private static void verifyProductCount(BasketItem basketItem) {
        if (basketItem.getProductCount() == 0) {
            basketItem.setProductCount(1);
        }
    }

    @Override
    public Mono<BasketItem> updatedBasketItem(BasketItem updatedBasketItem) {
        return basketItemRepository.save(updatedBasketItem);
    }

    @Override
    public BasketItem addNumberOfProducts(BasketItem basketItem, int numberOfProducts) {
        //todo refactor to functional solution  (Mono.zip ?)
        int currentProductCount = basketItem.getProductCount();
        basketItem.setProductCount(currentProductCount+numberOfProducts);
        updatedBasketItem(basketItem).subscribe();
        return basketItem;
    }

    @Override
    public BasketItem reduceNumberOfProducts(BasketItem basketItem, int numberOfProducts) {
        //todo refactor to functional solution  (Mono.zip ?)
        int currentProductCount = basketItem.getProductCount();
        basketItem.setProductCount(currentProductCount-numberOfProducts);
        updatedBasketItem(basketItem).subscribe();
        return basketItem;
    }

    @Override
    public Mono<Void> deleteBasketItemForId(Long basketItemId) {
        return basketItemRepository.deleteById(basketItemId);
    }

    @Override
    public Mono<Void> deleteBasketItemsForBasketId(Long basketId) {
        return basketItemRepository.deleteByBasketId(basketId);
    }

    @Override
    public BigDecimal getTotalPrice(BasketItem basketItem) {
        //todo refactor to functional/reactive solution
        BigDecimal price = basketItem.getProduct().getPrice();
        int productCount = basketItem.getProductCount();
        return price.multiply(BigDecimal.valueOf(productCount));
    }

    //todo create
}
