package com.example.erp.controller;

import com.example.erp.model.Product;
import com.example.erp.repository.ProductRepository;
import com.example.erp.service.BagistoSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/erp/products")
public class ProductController {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private BagistoSyncService syncService;

    @GetMapping
    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        Product savedProduct = repository.save(product);
        syncService.syncToBagisto(savedProduct.getId());
        return savedProduct;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductForBagisto(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
