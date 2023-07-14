package com.waldron.ecommerceservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@RequiredArgsConstructor
@Getter
@ToString
public class OrderDto {

    @NotNull(message = "basketId can not be empty")
    private final Long basketId;

    @NotNull(message = "Name can not be empty")
    private final String name;

    @NotNull(message = "Address can not be empty")
    private final String address;


}
