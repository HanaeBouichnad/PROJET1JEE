package org.sid.bankaccountservice;

import org.sid.bankaccountservice.dtos.BankAccountRequestDTO;
import org.sid.bankaccountservice.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients
public class BankAccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankAccountServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(BankAccountService bankAccountService) {
        return args -> {
            for (long i = 1; i <= 3; i++) {
                for (int j = 0; j < 5; j++) {
                    bankAccountService.addBankAccount(BankAccountRequestDTO.builder()
                            .type(Math.random() > 0.5 ? "CURRENT-ACCOUNT" : "SAVING-ACCOUNT")
                            .balance(1000 + Math.random() * 60000)
                            .customerId(i)
                            .build());
                }
            }
        };
    }
}
