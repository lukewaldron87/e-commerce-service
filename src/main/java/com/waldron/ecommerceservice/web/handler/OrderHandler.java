package com.waldron.ecommerceservice.web.handler;

import com.waldron.ecommerceservice.dto.OrderDto;
import com.waldron.ecommerceservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Component
@RequiredArgsConstructor
public class OrderHandler {

    private final OrderService orderService;
    private final Validator validator;

    public Mono<ServerResponse> getOrderForId(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return orderService.getOrderForId(id)
                .flatMap(order -> ok().contentType(APPLICATION_JSON).bodyValue(order))
                //todo is this needed
                .switchIfEmpty(notFound().build());
    }

    public Mono<ServerResponse> getTotalPriceForOrderId(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return orderService.getTotalPriceForOrderId(id)
                .flatMap(total -> ok().contentType(APPLICATION_JSON).bodyValue(total));
    }

    public Mono<ServerResponse> createOrderFromBasket(ServerRequest request) {
        return request.bodyToMono(OrderDto.class).doOnNext(this::validate)
                .flatMap(orderDto -> orderService.createOrderFromBasket(orderDto))
                .flatMap(order -> status(CREATED).contentType(APPLICATION_JSON).bodyValue(order));
    }

    private void validate(OrderDto orderDto){
        Errors errors = new BeanPropertyBindingResult(orderDto, "orderDto");
        validator.validate(orderDto, errors);
        if(errors.hasErrors()){
            throw new ServerWebInputException(errors.toString());
        }
    }
}
