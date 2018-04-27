package com.gailo22.springbootrxjs2.controller;

import com.gailo22.springbootrxjs2.model.Customer;
import com.gailo22.springbootrxjs2.service.CustomerService;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @GetMapping("/api/customers/seq")
    public Observable<Customer> getCustomersSequential() {
        List<String> names = Arrays.asList("Acme", "Oceanic", "Ana");

        List<Observable<Customer>> observables = names.stream()
                .map(name -> {
                    return Observable.just(name)
                            .map(input -> {
                                try {
                                    return delay(input);
                                } catch (Exception e) {
                                    throw e;
                                }
                            })
                            .onErrorReturnItem(new Customer("default", new Date()));
                }
        ).collect(Collectors.toList());

        Observable<Customer> observable = Observable.merge(observables)
                .doOnEach(x -> {
//                    System.out.println("onEach: " + x.getValue());
                });
        return observable;
    }

    @GetMapping("/api/customers/par")
    public Observable<Customer> getCustomersParallel() {
        List<String> names = Arrays.asList("Acme", "Oceanic", "Ana");

        List<Observable<Customer>> observables = names.stream()
                .map(name -> {
                    return Observable.just(name)
                            .map(input -> {
                                try {
                                    return delay(input);
                                } catch (Exception e) {
                                    throw e;
                                }
                            })
                            .onErrorReturnItem(new Customer("default", new Date()))
                            .subscribeOn(Schedulers.io());
                }
        ).collect(Collectors.toList());

        Observable<Customer> observable = Observable.merge(observables)
                .doOnEach(x -> {
//                    System.out.println("onEach: " + x.getValue());
                });
        return observable;
    }

    @GetMapping("/api/customers/{id}/service")
    public List<Customer> getCustomersService(@PathVariable String id) {
        return customerService.getCusotmers(id)
                .onErrorReturnItem(Collections.emptyList())
                .blockingFirst();
    }

    private Customer delay(String name) {
        try {
            TimeUnit.SECONDS.sleep(3);
            System.out.println(name + ": " + Thread.currentThread().getName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if ("Oceanic".equals(name)) throw new RuntimeException("errorrrooor");

        return new Customer(name, new Date());
    }
}
