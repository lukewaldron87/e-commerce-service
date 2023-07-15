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

        //todo refactor to functional solution

        Mono<Basket> basketMono = basketRepository.findById(basketId)
                .switchIfEmpty(Mono.error(new NotFoundException("Basket not found")));

        Map<Long, BasketItem> goodIdToBasketItemMap = new HashMap<>();

        basketItemService.getBasketItemsForBasketId(basketId).collectMap(
                basketItem -> basketItem.getProductId(),
                basketItem -> basketItem
        ).subscribe(goodIdToBasketItemMap::putAll);

        return basketMono.map(basket -> {basket.setGoodIdToBasketItemMap(goodIdToBasketItemMap); return basket;});
    }

    @Override
    public Mono<Basket> createBasketForProduct(BasketDto basketDto) {

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
        BasketItem basketItem = BasketItem.builder()
                .productId(basketDto.getProductId())
                .productCount(basketDto.getProductCount())
                .basketId(basket.getId()).build();
        basketItemService.createBasketItem(basketItem).subscribe();
        return basketItem;
    }

    @Override
    public Mono<Basket> addNumberOfProductsToBasket(Long basketId, Long productId, int numberOfProducts) {

        //todo add catch for if basket doesn't exists
        Mono<Basket> basketMono = getBasketForId(basketId);

        // increment product count
        basketMono.filter(basket -> basket.isProductInBasket(productId))
                .map(basket -> basket.getBasketItemForProductId(productId))
                .doOnNext(basketItem -> {
                    BasketItem updatedBasketItem = basketItemService.addNumberOfProducts(basketItem, numberOfProducts);
                    basketItem.setProductCount(updatedBasketItem.getProductCount());
                }).subscribe();

        // create new
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

        // todo only need to update the basket if I'm adding a new BasketItem
        return updateBasket(basketMono);
    }

    private Mono<Basket> updateBasket(Mono<Basket> basketMono){
        return basketMono.doOnNext(basket -> basketRepository.save(basket));
    }

    @Override
    public Mono<Basket> reduceNumberOfProductsInBasket(Long basketId, Long productId, int numberOfProducts) {

        //todo add catch for if basket doesn't exists
        Mono<Basket> basketMono = getBasketForId(basketId);

        // remove basket item from basket
        basketMono.filter(basket -> shouldRemoveProduct(productId, numberOfProducts, basket))
                .map(basket -> basket.removeBasketItem(productId))
                .doOnNext(basketItem -> basketItemService.deleteBasketItemForId(basketItem.getId()).subscribe())
                .subscribe();

        //decrement if number less than existing
        basketMono.filter(basket -> ! shouldRemoveProduct(productId, numberOfProducts, basket))
                .map(basket -> basket.getBasketItemForProductId(productId))
                .doOnNext(basketItem -> {
                    BasketItem updatedBasketItem = basketItemService.reduceNumberOfProducts(basketItem, numberOfProducts);
                    basketItem.setProductCount(updatedBasketItem.getProductCount());
                })
                .subscribe();

        return basketMono;
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
