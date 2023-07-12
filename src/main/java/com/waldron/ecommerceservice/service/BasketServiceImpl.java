package com.waldron.ecommerceservice.service;

import com.waldron.ecommerceservice.entity.Basket;
import com.waldron.ecommerceservice.entity.BasketItem;
import com.waldron.ecommerceservice.exception.NotFoundException;
import com.waldron.ecommerceservice.repository.BasketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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

    //todo createBasket with product

    private Mono<Basket> updateBasket(Mono<Basket> basketMono){
        //todo test the basket is updated in Postman
        return basketMono.doOnNext(basket -> basketRepository.save(basket));
    }

    @Override
    public Mono<Basket> addProductToBasket(Long basketId, Long productId, int numberOfProducts) {

        Mono<Basket> basketMono = getBasketForId(basketId);

        // increment product count
        basketMono.filter(basket -> basket.isProductInBasket(productId))
                .flatMap(basket -> Mono.just(basket.getBasketItemForProductId(productId)))
                //.doOnNext(basketItem -> basketItem.setProductId(99l)) //THIS WORKS
                .doOnNext(basketItem -> {
                    BasketItem updatedBasketItem = basketItemService.addNumberOfProducts(basketItem, numberOfProducts);
                    basketItem.setProductCount(updatedBasketItem.getProductCount());
                })
                .subscribe();

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

    //todo get total

    //todo remove x number of products

    //todo remove product

    //todo add get Map <Product, Integer> productsToCountMap
}
