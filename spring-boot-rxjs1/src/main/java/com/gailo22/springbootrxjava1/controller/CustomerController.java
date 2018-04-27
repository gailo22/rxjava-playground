package com.gailo22.springbootrxjava1.controller;

import com.gailo22.springbootrxjava1.domain.CustomerInfo;
import com.gailo22.springbootrxjava1.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/api/customers/{id}/seq")
    public CustomerInfo getCustomerSeq(@PathVariable String id) {
        return customerService.getCustomerSequential(id);
    }

    @GetMapping("/api/customers/{id}/par")
    public CustomerInfo getCustomerPar(@PathVariable String id) {
        return customerService.getCustomerParallel(id);
    }
}
