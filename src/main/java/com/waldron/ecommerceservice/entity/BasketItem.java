package com.waldron.ecommerceservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@AllArgsConstructor
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

    //this constructor allows reading from the database while ignoring the @Transient fields
    @PersistenceConstructor
    public BasketItem(Long id, Long productId, int productCount, Long basketId) {
        this.id = id;
        this.productId = productId;
        this.productCount = productCount;
        this.basketId = basketId;
    }
}
