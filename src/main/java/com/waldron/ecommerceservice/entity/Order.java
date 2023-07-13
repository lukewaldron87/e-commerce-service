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

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@ToString
@Table("customer_order")
public class Order {

    @Id
    private Long id;

    @Transient
    private Set<BasketItem> BasketItems;

    @Column("status")
    private Status status;

    @Column("name")
    private String name;

    @Column("address")
    private String address;

    @PersistenceCreator
    public Order(Long id, Status status, String name, String address) {
        this.id = id;
        this.status = status;
        this.name = name;
        this.address = address;
    }
}
