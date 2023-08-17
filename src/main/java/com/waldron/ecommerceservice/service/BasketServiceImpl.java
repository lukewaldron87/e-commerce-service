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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;

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
        //todo refactor to reactive solution

        AtomicLong productId = new AtomicLong();

        //todo change to save basketItems (many) before saving basket (one)
        //https://stackoverflow.com/questions/59986014/how-to-implement-onetomany-manytoone-and-manytomany-with-r2dbc-in-a-project-whi

        //create basket item
        return basketItemDtoMono
                .map(basketDto -> {
                    // map dto to entity
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
                // create basket
                .map(basketRepository::save)
                .flatMap(basketMono -> basketMono)
                // add basketId to BasketItem and update
                .map(basket -> {
                    BasketItem basketItem = basket.getBasketItemForProductId(productId.get());
                    basketItem.setBasketId(basket.getId());
                    //update basketItem
                    basketItemService.updatedBasketItem(basketItem).subscribe();
                    return basket;
                });



//        Basket basket = createBasket();
//        Mono<BasketItem> basketItemMono = createBasketItem(basketDto, basket);
//        basketItemMono.subscribe(basketItem -> basket.addBasketItemForProductId(basketItem.getProductId(), basketItem));
//        return Mono.just(basket);
    }

    private Basket createBasket() {
        Basket basket = Basket.builder().build();
        basketRepository.save(basket).subscribe();
        return basket;
    }

    private Mono<BasketItem> createBasketItem(Mono<BasketItemDto> basketItemDtoMono, Basket basket) {
        //todo refactor to functional/reactive solution

        return basketItemDtoMono.map(basketDto -> {
            return BasketItem.builder()
                    .productId(basketDto.getProductId())
                    .productCount(basketDto.getProductCount())
                    .basketId(basket.getId()).build();
        }).flatMap(basketItem -> basketItemService.createBasketItem(basketItem));
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

        //todo refactor to functional/reactive solution

        //todo add catch for if basket doesn't exists
        Mono<Basket> basketMono = getBasketForId(basketId);
        incrementProductCount(productId, numberOfProducts, basketMono);
        createNewBasketItemForProduct(basketId, productId, numberOfProducts, basketMono);

        // todo only need to update the basket if I'm adding a new BasketItem
        return updateBasket(basketMono);
    }

    private void incrementProductCount(Long productId, int numberOfProducts, Mono<Basket> basketMono) {
        basketMono.filter(basket -> basket.isProductInBasket(productId))
                .map(basket -> basket.getBasketItemForProductId(productId))
                .doOnNext(basketItem -> {
                    BasketItem updatedBasketItem = basketItemService.addNumberOfProducts(basketItem, numberOfProducts);
                    basketItem.setProductCount(updatedBasketItem.getProductCount());
                }).subscribe();
    }

    // must return a Mono to maintain backpressure
    private void createNewBasketItemForProduct(Long basketId,
                                               Long productId,
                                               int numberOfProducts,
                                               Mono<Basket> basketMono) {
        basketMono.filter(basket -> !basket.isProductInBasket(productId))
                .map(basket -> {
                    BasketItem basketItem = BasketItem.builder()
                            .productId(productId)
                            //todo how to add product from MONO
                            //.product(product)
                            .productCount(numberOfProducts)
                            .basketId(basketId)
                            .build();
                    basketItemService.createBasketItem(basketItem).subscribe();
                    basket.addBasketItemForProductId(productId, basketItem);
                    return basket;
                }).subscribe();
    }

    private Mono<Basket> updateBasket(Mono<Basket> basketMono){
        return basketMono.doOnNext(basket -> basketRepository.save(basket));
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

        //todo add catch for if basket doesn't exists
        //todo refactor to functional solution (Mono.zip ?)
        //todo ensure all methods are returning and adding to the backpressure
        return getBasketForId(basketId)
                .flatMap(basket ->
                    shouldRemoveProduct(productId, numberOfProducts, basket)
                ? removeBasketItemFromBasket(productId, numberOfProducts, basket)
                : reduceNumberOfProductInBasket(productId, numberOfProducts, basket)
                )
                //todo remove this and return Mono<Void>. Then return link to the getId operation
                .then(getBasketForId(basketId));
        // should i return mono empty and then use getById in controller

        //todo do i need to update in the database?
    }

    private Mono<Void> removeBasketItemFromBasket(Long productId, int numberOfProducts, Basket basket) {
        return basketItemService.deleteBasketItemForId(basket.removeBasketItem(productId).getId());
    }

    /**
     * decrement the product count if number less than existing
     *
     * @param productId
     * @param numberOfProducts
     * @param basket
     */
    private Mono<Void> reduceNumberOfProductInBasket(Long productId, int numberOfProducts, Basket basket) {
        BasketItem basketItem = basket.getBasketItemForProductId(productId);
        BasketItem updatedBasketItem = basketItemService.reduceNumberOfProducts(basketItem, numberOfProducts);
        basketItem.setProductCount(updatedBasketItem.getProductCount());
        return Mono.empty();
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
