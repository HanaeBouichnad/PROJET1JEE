package org.sid.customerservice;

import org.sid.customerservice.dtos.CustomerRequestDTO;
import org.sid.customerservice.entities.Customer;
import org.sid.customerservice.services.CustomerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(CustomerService customerService) {
        return args -> {
            List<String> names = List.of("Hanae", "Mehdi", "Ibtissam");
            names.forEach(name -> {
                customerService.addCustomer(CustomerRequestDTO.builder()
                        .name(name)
                        .email(name + "@gmail.com")
                        .build());
            });
        };
    }
}
