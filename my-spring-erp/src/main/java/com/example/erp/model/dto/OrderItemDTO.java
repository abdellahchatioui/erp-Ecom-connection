package com.example.erp.model.dto;

import lombok.Data;

@Data
public class OrderItemDTO {
    private Long product_id;
    private String sku;
    private String name;
    private Integer qty;
    private Double price;
    private Double total;
}
