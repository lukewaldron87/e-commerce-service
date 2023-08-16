package com.waldron.ecommerceservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Builder
@RequiredArgsConstructor
public class BasketItemDto {

    @NotNull(message = "productId can not be empty")
    private final Long productId;

    @NotNull(message = "productCount can not be empty")
    private final int productCount;
}
