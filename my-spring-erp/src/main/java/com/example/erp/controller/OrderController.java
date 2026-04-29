package com.example.erp.controller;

import com.example.erp.model.ErpOrder;
import com.example.erp.model.ErpOrderItem;
import com.example.erp.model.dto.OrderDTO;
import com.example.erp.repository.ErpOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/erp/orders")
public class OrderController {

    @Autowired
    private ErpOrderRepository orderRepository;

    @Value("${bagisto.api.key}")
    private String apiKey;

    @PostMapping
    public ResponseEntity<?> receiveOrder(@RequestHeader(value = "X-ERP-TOKEN", required = false) String token, 
                                          @RequestBody OrderDTO orderDTO) {
        
        // Simple security check matching Bagisto's middleware
        if (token == null || !token.equals(apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing X-ERP-TOKEN");
        }

        try {
            // Map DTO to Entity
            ErpOrder order = new ErpOrder();
            order.setBagistoOrderId(orderDTO.getOrder_id());
            order.setIncrementId(orderDTO.getIncrement_id());
            order.setStatus(orderDTO.getStatus());
            order.setCustomerEmail(orderDTO.getCustomer_email());
            order.setCustomerName(orderDTO.getCustomer_name());
            order.setGrandTotal(orderDTO.getGrand_total());
            order.setBaseGrandTotal(orderDTO.getBase_grand_total());
            order.setBagistoCreatedAt(orderDTO.getCreated_at());

            if (orderDTO.getItems() != null) {
                var items = orderDTO.getItems().stream().map(dto -> {
                    ErpOrderItem item = new ErpOrderItem();
                    item.setBagistoProductId(dto.getProduct_id());
                    item.setSku(dto.getSku());
                    item.setName(dto.getName());
                    item.setQty(dto.getQty());
                    item.setPrice(dto.getPrice());
                    item.setTotal(dto.getTotal());
                    item.setOrder(order);
                    return item;
                }).collect(Collectors.toList());

                order.setItems(items);
            }

            // Save to database
            ErpOrder savedOrder = orderRepository.save(order);
            
            System.out.println("Successfully received and saved Bagisto Order: " + savedOrder.getIncrementId());

            return ResponseEntity.ok().body("Order received successfully");
            
        } catch (Exception e) {
            System.err.println("Failed to process order: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing order");
        }
    }
}
