package com.Luxa.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InventoryApplication {
    public static void main(String[] args) {
        System.out.println("DEBUG DATASOURCE URL: " + System.getenv("SPRING_DATASOURCE_URL"));
        System.out.println("DEBUG DATASOURCE USERNAME: " + System.getenv("SPRING_DATASOURCE_USERNAME"));
        SpringApplication.run(InventoryApplication.class, args);
    }
}
