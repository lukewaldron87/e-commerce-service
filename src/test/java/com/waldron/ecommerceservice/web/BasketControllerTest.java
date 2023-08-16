package com.waldron.ecommerceservice.web;

import com.waldron.ecommerceservice.service.BasketService;
import com.waldron.ecommerceservice.web.controller.BasketController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.ArgumentMatchers.any;

@WebFluxTest(BasketController.class)
class BasketControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private BasketService basketService;

    private static String BASKETS_URI = "/baskets";


}