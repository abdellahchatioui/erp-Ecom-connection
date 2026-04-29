package com.example.erp.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderDTO {
    private Long order_id;
    private String increment_id;
    private String status;
    private String customer_email;
    private String customer_name;
    private Double grand_total;
    private Double base_grand_total;
    private String created_at;
    private List<OrderItemDTO> items;
    private AddressDTO billing_address;
    private AddressDTO shipping_address;
}
