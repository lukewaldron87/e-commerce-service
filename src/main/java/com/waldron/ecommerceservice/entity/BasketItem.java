package com.waldron.ecommerceservice.entity;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@ToString
@Table("basket_item")
public class BasketItem {

    @Id
    private Long id;

    // relationships not supported in R2DBC. I'm using a Long to store the productId foreign key
    @Column("product_id")
    private Long productId;

    @Transient //ignore this field when mapping
    private Product product;

    @Column("product_count")
    private int productCount;

    // ManyToOne
    @Column("basket_id")
    private Long basketId;

}
