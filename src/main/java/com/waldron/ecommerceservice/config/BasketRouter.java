package com.waldron.ecommerceservice.config;

import com.waldron.ecommerceservice.web.handler.BasketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class BasketRouter {

    public static final String BASKETS_URI = "/baskets";
    private static final String BASKETS_URI_ID = BASKETS_URI+"/{id}";
    private static final String BASKETS_URI_TOTAL = BASKETS_URI_ID+"/total";

    @Bean
    public RouterFunction<ServerResponse> basketRoutes(BasketHandler handler){
        return route()
                .GET(BASKETS_URI_ID, accept(APPLICATION_JSON), handler::getBasketForId)
                .GET(BASKETS_URI_TOTAL, accept(APPLICATION_JSON), handler::getTotalPriceForBasketId)
                //.POST(BASKETS_URI, accept(APPLICATION_JSON), handler::createBasketForProduct)
                //addNumberOfProductsToBasket
                //reduceNumberOfProductsInBasket
                .build();
    }

}
