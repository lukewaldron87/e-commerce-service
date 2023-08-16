package com.waldron.ecommerceservice.config;

import com.waldron.ecommerceservice.web.handler.OrderHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class OrderRouter {

    public static final String ORDERS_URI = "/orders";
    private static final String ORDERS_URI_ID = ORDERS_URI + "/{id}";
    private static final String ORDERS_URL_TOTAL = ORDERS_URI_ID + "/total";

    @Bean
    public RouterFunction<ServerResponse> orderRoutes(OrderHandler handler){
        return route()
                .GET(ORDERS_URI_ID, accept(APPLICATION_JSON), handler::getOrderForId)
                .GET(ORDERS_URL_TOTAL, accept(APPLICATION_JSON), handler::getTotalPriceForOrderId)
                .POST(ORDERS_URI, accept(APPLICATION_JSON), handler::createOrderFromBasket)
                .build();
    }
}
