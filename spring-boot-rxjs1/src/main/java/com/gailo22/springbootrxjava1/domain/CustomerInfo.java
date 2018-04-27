package com.gailo22.springbootrxjava1.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CustomerInfo {
    private String custId;
    private String custName;
    private List<Address> addresses;
    private List<Account> accounts;
    private List<Product> products;
    private long time;
}
