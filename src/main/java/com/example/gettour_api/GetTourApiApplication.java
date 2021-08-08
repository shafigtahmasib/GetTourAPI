package com.example.gettour_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GetTourApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(GetTourApiApplication.class, args);
    }
}
