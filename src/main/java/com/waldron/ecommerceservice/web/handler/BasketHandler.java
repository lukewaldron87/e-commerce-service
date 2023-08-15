package com.waldron.ecommerceservice.web.handler;

import com.waldron.ecommerceservice.dto.BasketDto;
import com.waldron.ecommerceservice.service.BasketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.ServerResponse.notFound;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
@RequiredArgsConstructor
public class BasketHandler {

    private final BasketService basketService;
    private final Validator validator;

    public Mono<ServerResponse> getBasketForId(ServerRequest request){
        Long id = Long.valueOf(request.pathVariable("id"));

        return basketService.getBasketForId(id)
                .flatMap(basket -> ok().contentType(APPLICATION_JSON).bodyValue(basket))
                .switchIfEmpty(notFound().build());
    }

    // todo improve the output eg {total price: 19.99}
    public Mono<ServerResponse> getTotalPriceForBasketId(ServerRequest request){
        Long id = Long.valueOf(request.pathVariable("id"));

        return basketService.getTotalPriceForBasketId(id)
                .flatMap(total -> ok().contentType(APPLICATION_JSON).bodyValue(total))
                .switchIfEmpty(notFound().build());
    }

//    public Mono<ServerResponse> createBasketForProduct(ServerRequest request) {
//        Mono<BasketDto> basketDtoMono = request.bodyToMono(BasketDto.class).doOnNext(this::validate);
//        return ok().build(basketService.createBasketForProduct(basketDtoMono));
//    }

    private void validate(BasketDto basketDto){
        Errors errors = new BeanPropertyBindingResult(basketDto, "basketDto");
        validator.validate(basketDto, errors);
        if(errors.hasErrors()){
            throw new ServerWebInputException(errors.toString());
        }
    }

    //todo
    //addNumberOfProductsToBasket
    //reduceNumberOfProductsInBasket
}
