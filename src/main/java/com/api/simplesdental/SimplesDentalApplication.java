package com.api.simplesdental;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class SimplesDentalApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimplesDentalApplication.class, args);
    }
}
