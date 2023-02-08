package com.learning;

import com.learning.model.Inventory;
import com.learning.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@EnableEurekaClient
@SpringBootApplication
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadData(InventoryRepository inventoryRepository) {
        return args -> {
            Inventory inventory = Inventory.builder().skuCode("iphone_13").quantity(100).build();
            Inventory inventory1 = Inventory.builder().skuCode("iphone_13_red").quantity(0).build();
            inventoryRepository.save(inventory);
            inventoryRepository.save(inventory1);
        };
    }
}
