package com.waldron.ecommerceservice.web.handler;

import com.waldron.ecommerceservice.entity.Product;
import com.waldron.ecommerceservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Component
@RequiredArgsConstructor
public class ProductHandler {

    private final ProductService productService;
    private final Validator validator;

    public Mono<ServerResponse> getProducts(ServerRequest request) {
        Flux<Product> products = productService.getProducts();
        return ok().contentType(APPLICATION_JSON).body(products, Product.class);
    }

    public Mono<ServerResponse> createProduct(ServerRequest request) {
        Mono<Product> productMono = request.bodyToMono(Product.class).doOnNext(this::validate);
        return productService.createProduct(productMono)
                .flatMap(person -> status(HttpStatus.CREATED).contentType(APPLICATION_JSON).bodyValue(person));

        //todo add get product for ID and return 201 created
        //        .flatMap(product -> created(URI.create(ProductRouter.PRODUCTS_URL + "/" + product.getId())).build());
    }

    public Mono<ServerResponse> updateProductForId(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return request.bodyToMono(Product.class).doOnNext(this::validate)
                .flatMap(product -> productService.updateProductForId(id, product))
                .flatMap(person -> ok().contentType(APPLICATION_JSON).bodyValue(person))
                .switchIfEmpty(notFound().build());
    }

    private void validate(Product product){
        Errors errors = new BeanPropertyBindingResult(product, "product");
        validator.validate(product, errors);
        if(errors.hasErrors()){
            throw new ServerWebInputException(errors.toString());
        }
    }

    public Mono<ServerResponse> deleteProductForId(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return productService.deleteProductForId(id)
                .flatMap(voidMono ->  ok().build());
    }

}
