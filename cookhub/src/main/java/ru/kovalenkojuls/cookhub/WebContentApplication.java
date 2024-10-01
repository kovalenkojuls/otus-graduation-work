package ru.kovalenkojuls.cookhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "ru.kovalenkojuls.cookhub")
public class WebContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebContentApplication.class, args);
    }
}