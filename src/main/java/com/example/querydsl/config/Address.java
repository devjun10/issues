package com.example.querydsl.config;

import lombok.Data;

@Data
public class Address {
    private String city;
    private String street;
    private String code;

    public Address(String city, String street, String code) {
        this.city = city;
        this.street = street;
        this.code = code;
    }
}
