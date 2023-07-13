package com.waldron.ecommerceservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@AllArgsConstructor
@ToString
@Table("order_item")
public class OrderItem {

    @Id
    private Long id;

    @Column("product_id")
    private Long productId;

    @Transient
    private Product product;

    @Column("product_count")
    private int productCount;

    // ManyToOne
    @Column("order_id")
    private Long orderId;

    @PersistenceCreator
    public OrderItem(Long id, Long productId, int productCount, Long orderId) {
        this.id = id;
        this.productId = productId;
        this.productCount = productCount;
        this.orderId = orderId;
    }
}
