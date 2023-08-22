package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.dto.BasketItemDto;
import com.waldron.ecommerceservice.entity.Basket;
import com.waldron.ecommerceservice.entity.BasketItem;
import com.waldron.ecommerceservice.exception.NotFoundException;
import com.waldron.ecommerceservice.repository.BasketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class BasketServiceImpl implements BasketService{

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private BasketItemService basketItemService;

    @Autowired
    private ProductService productService;

    @Override
    public Mono<Basket> getBasketForId(Long basketId) {

        //todo how to stream list of BasketItems and add them one at at time to Basket?

        return basketRepository.findById(basketId)
                .switchIfEmpty(Mono.error(new NotFoundException("Basket not found")))
                .zipWith(basketItemService.getBasketItemsForBasketId(basketId)
                                .collectMap(
                                        basketItem -> basketItem.getProductId(),
                                        basketItem -> basketItem
                                ),
                        (basket, basketItemMap) ->  {
                            basket.setGoodIdToBasketItemMap(basketItemMap);
                            return basket;
                        });
    }

    /**
     * Create a new basket for the given product
     *
     * @param basketItemDtoMono
     * @return
     */
    @Override
    public Mono<Basket> createBasketForProduct(Mono<BasketItemDto> basketItemDtoMono) {

        //todo refactor to cleaner solution
        AtomicLong productId = new AtomicLong();

        //create basket item
        return basketItemDtoMono
                // map dto to entity
                .map(basketDto -> {
                    productId.set(basketDto.getProductId());
                    return BasketItem.builder()
                            .productId(basketDto.getProductId())
                            .productCount(basketDto.getProductCount())
                            .build();
                }).flatMap(basketItemService::createBasketItem)
                // add basket item to new basket
                .map(basketItem -> {
                    Basket basket = Basket.builder().build();
                    basket.addBasketItemForProductId(basketItem.getProductId(), basketItem);
                    return basket;
                })
                .map(basketRepository::save)
                .flatMap(basketMono -> basketMono)
                // add basketId to BasketItem and update
                .map(basket -> {
                    BasketItem basketItem = basket.getBasketItemForProductId(productId.get());
                    basketItem.setBasketId(basket.getId());
                    //todo how to return subscription to this operation and then return the basket
                    return basketItemService.updatedBasketItem(basketItem)
                            .zipWith(Mono.just(basket));
                })
                .flatMap(tuple2Mono -> tuple2Mono)
                .map(tuple2 -> tuple2.getT2());

    }

    /**
     * Add the given number of the specified product to the basket. If the product is not already in the basket it will be added.
     *
     * @param basketId
     * @param productId
     * @param numberOfProducts
     * @return
     */
    @Override
    public Mono<Basket> addNumberOfProductsToBasket(Long basketId, Long productId, int numberOfProducts) {
        return getBasketForId(basketId)
                .flatMap(basket -> basket.isProductInBasket(productId)
                        ? incrementProductCount(productId, numberOfProducts, basket)
                        : createNewBasketItemForProduct(basketId, productId, numberOfProducts, basket)
                );
    }

    private Mono<Basket> incrementProductCount(Long productId, int numberOfProducts, Basket basket) {
        return Mono.just(basket.getBasketItemForProductId(productId))
                .flatMap(basketItem -> basketItemService.addNumberOfProducts(basketItem, numberOfProducts))
                .map(basketItem -> {
                    basket.addBasketItemForProductId(productId, basketItem);
                    return basket;
                });//todo am I subscribing to the correct operation here? Am I breaking the chain?
    }

    // must return a Mono to maintain backpressure
    private Mono<Basket> createNewBasketItemForProduct(Long basketId,
                                                       Long productId,
                                                       int numberOfProducts,
                                                       Basket basket) {
        //todo should I use a zip for the basket and basketItem?
        // I have used the pattern below in all add/removed product methods
        return Mono.just(BasketItem.builder()
                        .productId(productId)
                        .productCount(numberOfProducts)
                        .basketId(basketId)
                        .build())
                .flatMap(basketItemService::createBasketItem)
                .map(basketItem -> {
                    basket.addBasketItemForProductId(productId, basketItem);
                    return basket;
                });
    }


    /**
     * Remove the given number of the specified product from the basket.
     * If number of products is greater than or equal to the number in the basket that product will be removed.
     *
     * @param basketId
     * @param productId
     * @param numberOfProducts
     * @return
     */
    @Override
    public Mono<Basket> reduceNumberOfProductsInBasket(Long basketId, Long productId, int numberOfProducts) {
        return getBasketForId(basketId)
                .flatMap(basket ->
                        shouldRemoveProduct(productId, numberOfProducts, basket)
                                ? removeBasketItemFromBasket(productId, basket)
                                : reduceNumberOfProductInBasket(productId, numberOfProducts, basket)
                );
    }

    private Mono<Basket> removeBasketItemFromBasket(Long productId, Basket basket) {
        return Mono.just(basket.removeBasketItem(productId))
                .flatMap(basketItem -> basketItemService.deleteBasketItemForId(basketItem.getId()))
                .thenReturn(basket);//todo am I subscribing to the correct operation here? Am I breaking the chain?
    }

    /**
     * decrement the product count if number less than existing
     *
     * @param productId
     * @param numberOfProducts
     * @param basket
     */
    private Mono<Basket> reduceNumberOfProductInBasket(Long productId, int numberOfProducts, Basket basket) {
        return Mono.just(basket.getBasketItemForProductId(productId))
                .flatMap(basketItem -> basketItemService.reduceNumberOfProducts(basketItem, numberOfProducts))
                .map(basketItem -> {
                    basket.addBasketItemForProductId(productId, basketItem);
                    return basket;
                });//todo am I subscribing to the correct operation here? Am I breaking the chain?

    }

    private static boolean shouldRemoveProduct(Long productId, int numberOfProducts, Basket basket) {
        // todo getting null pointer here in logs.
        return numberOfProducts >= basket.getBasketItemForProductId(productId).getProductCount();
    }

    @Override
    public Mono<BigDecimal> getTotalPriceForBasketId(Long basketId) {

        return getBasketForId(basketId)
                .map(basket -> basket.getGoodIdToBasketItemMap().values().stream()
                        .map(basketItem -> basketItemService.getTotalPrice(basketItem))
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    @Override
    public Mono<Void> deleteBasketForId(Long basketId) {
        return basketItemService.deleteBasketItemsForBasketId(basketId)
                .then(basketRepository.deleteById(basketId));
    }
}
