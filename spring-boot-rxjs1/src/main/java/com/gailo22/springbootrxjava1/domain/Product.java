package com.gailo22.springbootrxjava1.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Product {
    private String productName;
}
