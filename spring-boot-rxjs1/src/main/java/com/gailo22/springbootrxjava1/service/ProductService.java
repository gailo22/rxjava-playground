package com.gailo22.springbootrxjava1.service;

import com.gailo22.springbootrxjava1.domain.Account;
import com.gailo22.springbootrxjava1.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Service
public class ProductService {

    @Autowired
    private Executor executor;

    public Observable<List<Product>> getProducts(String custId) {
        return Observable.fromCallable(
                () ->  {
                    try {
                        TimeUnit.SECONDS.sleep(3);
                        System.out.println(Thread.currentThread().getName());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return Collections.singletonList(Product.builder().productName("savings").build());
                }
        ).subscribeOn(Schedulers.from(executor));
    }
}
