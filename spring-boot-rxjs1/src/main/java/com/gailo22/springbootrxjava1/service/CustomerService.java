package com.gailo22.springbootrxjava1.service;

import com.gailo22.springbootrxjava1.domain.Account;
import com.gailo22.springbootrxjava1.domain.Address;
import com.gailo22.springbootrxjava1.domain.CustomerInfo;
import com.gailo22.springbootrxjava1.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Service
public class CustomerService {

    @Autowired
    private AddressService addressService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ProductService productService;

    public CustomerInfo getCustomerSequential(String id) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        CountDownLatch latch = new CountDownLatch(1);

        Observable<List<Address>> addressesObservable = addressService.getAddresses(id);
        Observable<List<Account>> accountsObservable = accountService.getAccounts(id);
        Observable<List<Product>> productsObservable = productService.getProducts(id);

        CustomerInfo.CustomerInfoBuilder builder = CustomerInfo.builder();
        builder.custId(id).custName("admin");

        addressesObservable.subscribe(a -> {
            accountsObservable.subscribe(b -> {
                productsObservable.subscribe(c -> {
                    builder.addresses(a);
                    builder.accounts(b);
                    builder.products(c);
                    latch.countDown();
                });
            });
        });

        waitForAll(latch);

        stopWatch.stop();
        long time = stopWatch.getTotalTimeMillis();
        System.out.println("Time: " + time);
        return builder.time(time).build();
    }

    public CustomerInfo getCustomerParallel(String id) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        CountDownLatch latch = new CountDownLatch(1);

        Observable<List<Address>> addressesObservable = addressService.getAddresses(id);
        Observable<List<Account>> accountsObservable = accountService.getAccounts(id);
        Observable<List<Product>> productsObservable = productService.getProducts(id);

        CustomerInfo.CustomerInfoBuilder builder = CustomerInfo.builder();
        builder.custId(id).custName("admin");

        Observable.zip(addressesObservable, accountsObservable, productsObservable, (a, b, c) -> {
            builder.addresses(a);
            builder.accounts(b);
            builder.products(c);
            return builder;
        }).subscribe(b -> {
            System.out.println("b: " + b);
            latch.countDown();
        }, t -> {
            latch.countDown();
        });

        waitForAll(latch);

        stopWatch.stop();
        long time = stopWatch.getTotalTimeMillis();
        System.out.println("Time: " + time);
        return builder.time(time).build();

    }

    private void waitForAll(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
