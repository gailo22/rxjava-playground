package com.gailo22.springbootrxjava1.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Address {
    private String postcode;
    private String street;
    private String city;
    private String country;
}
