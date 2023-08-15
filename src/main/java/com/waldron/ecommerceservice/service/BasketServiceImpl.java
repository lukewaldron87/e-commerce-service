package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.dto.BasketDto;
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

        //todo refactor to functional solution (Mono.zip ?)

        Mono<Basket> basketMono = basketRepository.findById(basketId)
                .switchIfEmpty(Mono.error(new NotFoundException("Basket not found")));

        Map<Long, BasketItem> goodIdToBasketItemMap = new HashMap<>();

        basketItemService.getBasketItemsForBasketId(basketId).collectMap(
                basketItem -> basketItem.getProductId(),
                basketItem -> basketItem
        ).subscribe(goodIdToBasketItemMap::putAll);

        return basketMono.map(basket -> {basket.setGoodIdToBasketItemMap(goodIdToBasketItemMap); return basket;});
    }

    /**
     * Create a new basket for the given product
     *
     * @param basketDto
     * @return
     */
    @Override
    public Mono<Basket> createBasketForProduct(BasketDto basketDto) {
        //todo refactor to functional/reactive solution
        Basket basket = createBasket();
        BasketItem basketItem = createBasketItem(basketDto, basket);
        basket.addBasketItemForProductId(basketItem.getProductId(), basketItem);
        return Mono.just(basket);
    }

    private Basket createBasket() {
        Basket basket = Basket.builder().build();
        basketRepository.save(basket).subscribe();
        return basket;
    }

    private BasketItem createBasketItem(BasketDto basketDto, Basket basket) {
        //todo refactor to functional/reactive solution
        BasketItem basketItem = BasketItem.builder()
                .productId(basketDto.getProductId())
                .productCount(basketDto.getProductCount())
                .basketId(basket.getId()).build();
        basketItemService.createBasketItem(basketItem).subscribe();
        return basketItem;
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
        Mono<Basket> basketMono = getBasketForId(basketId);
        removeBasketItemFromBasket(productId, numberOfProducts, basketMono);
        reduceNumberOfProductInBasket(productId, numberOfProducts, basketMono);
        return basketMono;
    }

    private void removeBasketItemFromBasket(Long productId, int numberOfProducts, Mono<Basket> basketMono) {
        basketMono.filter(basket -> shouldRemoveProduct(productId, numberOfProducts, basket))
                .map(basket -> basket.removeBasketItem(productId))
                .doOnNext(basketItem -> basketItemService.deleteBasketItemForId(basketItem.getId()).subscribe())
                .subscribe();
    }

    /**
     * decrement the product count if number less than existing
     *
     * @param productId
     * @param numberOfProducts
     * @param basketMono
     */
    private void reduceNumberOfProductInBasket(Long productId, int numberOfProducts, Mono<Basket> basketMono) {
        basketMono.filter(basket -> ! shouldRemoveProduct(productId, numberOfProducts, basket))
                .map(basket -> basket.getBasketItemForProductId(productId))
                .doOnNext(basketItem -> {
                    BasketItem updatedBasketItem = basketItemService.reduceNumberOfProducts(basketItem, numberOfProducts);
                    basketItem.setProductCount(updatedBasketItem.getProductCount());
                })
                .subscribe();
    }

    private static boolean shouldRemoveProduct(Long productId, int numberOfProducts, Basket basket) {
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
