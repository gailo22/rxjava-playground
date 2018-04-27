package com.gailo22.springbootrxjava1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@SpringBootApplication
public class SpringBootRxJava1Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootRxJava1Application.class, args);
	}

	@Bean
	public Executor executor() {
		return Executors.newFixedThreadPool(6);
	}
}
