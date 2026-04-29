package com.example.erp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class BagistoSyncService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${bagisto.api.url}")
    private String bagistoUrl;

    @Value("${bagisto.api.key}")
    private String apiKey;

    public void syncToBagisto(String sku) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-ERP-TOKEN", apiKey); // Changed header to match Bagisto expectation
        headers.set("Accept", "application/json");

        Map<String, Object> body = new HashMap<>();
        body.put("sku", sku);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                bagistoUrl + "/api/erp/webhook/product", 
                request, 
                String.class
            );
            System.out.println("Bagisto Sync Success: " + response.getBody());
        } catch (Exception e) {
            System.err.println("Bagisto Sync Failed: " + e.getMessage());
        }
    }
}
