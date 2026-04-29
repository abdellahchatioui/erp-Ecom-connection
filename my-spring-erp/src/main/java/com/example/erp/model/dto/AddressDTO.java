package com.example.erp.model.dto;

import lombok.Data;

@Data
public class AddressDTO {
    private String first_name;
    private String last_name;
    private String email;
    private String address1;
    private String city;
    private String state;
    private String postcode;
    private String country;
    private String phone;
}
