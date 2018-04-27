package com.gailo22.springbootrxjs2.service;

import com.gailo22.springbootrxjs2.model.Customer;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CustomerService {

    public Observable<List<Customer>> getCusotmers(String custId) {
        return Observable.fromCallable(
                () ->  {
                    try {
                        TimeUnit.SECONDS.sleep(3);
                        System.out.println(Thread.currentThread().getName());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if ("1".equals(custId)) throw new RuntimeException("eeeeerrrorrr");
                    return Collections.singletonList(new Customer("John", new Date()));
                }
        ).subscribeOn(Schedulers.io());
    }
}
