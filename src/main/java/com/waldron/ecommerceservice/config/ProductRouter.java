package com.waldron.ecommerceservice.config;

import com.waldron.ecommerceservice.web.handler.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ProductRouter {

    public static final String PRODUCTS_URL = "/products";

    public static final String PRODUCTS_URL_ID = PRODUCTS_URL+"/{id}";

    @Bean
    public RouterFunction<ServerResponse> productRouts(ProductHandler handler){
        return route()
                .GET(PRODUCTS_URL, accept(APPLICATION_JSON), handler::getProducts)
                .POST(PRODUCTS_URL, accept(APPLICATION_JSON), handler::createProduct)
                .PUT(PRODUCTS_URL_ID, accept(APPLICATION_JSON), handler::updateProductForId)
                .DELETE(PRODUCTS_URL_ID, accept(APPLICATION_JSON), handler::deleteProductForId)
                .build();

    }
}
