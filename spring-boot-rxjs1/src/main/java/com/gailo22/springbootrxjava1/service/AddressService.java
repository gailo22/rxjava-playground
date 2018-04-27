package com.gailo22.springbootrxjava1.service;

import com.gailo22.springbootrxjava1.domain.Account;
import com.gailo22.springbootrxjava1.domain.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Service
public class AddressService {

    @Autowired
    private Executor executor;

    public Observable<List<Address>> getAddresses(String custId) {
        return Observable.fromCallable(
                () ->  {
                    try {
                        TimeUnit.SECONDS.sleep(3);
                        System.out.println(Thread.currentThread().getName());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if ("1".equals(custId)) throw new RuntimeException("eeeeerrrorrr");
                    return Collections.singletonList(Address.builder().country("Thailand").build());
                }
        ).subscribeOn(Schedulers.from(executor));
    }
}
