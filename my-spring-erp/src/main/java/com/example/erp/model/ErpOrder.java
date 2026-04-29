package com.example.erp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "erp_orders")
@Data
public class ErpOrder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long bagistoOrderId;
    private String incrementId;
    private String status;
    private String customerEmail;
    private String customerName;
    private Double grandTotal;
    private Double baseGrandTotal;
    private String bagistoCreatedAt;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order", orphanRemoval = true)
    private List<ErpOrderItem> items;
}
