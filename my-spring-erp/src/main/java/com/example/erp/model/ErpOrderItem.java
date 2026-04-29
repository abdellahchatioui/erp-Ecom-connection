package com.example.erp.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "erp_order_items")
@Data
public class ErpOrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private ErpOrder order;

    private Long bagistoProductId;
    private String sku;
    private String name;
    private Integer qty;
    private Double price;
    private Double total;
}
