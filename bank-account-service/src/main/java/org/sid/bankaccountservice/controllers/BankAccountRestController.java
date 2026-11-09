package org.sid.bankaccountservice.controllers;

import org.sid.bankaccountservice.dtos.BankAccountRequestDTO;
import org.sid.bankaccountservice.dtos.BankAccountResponseDTO;
import org.sid.bankaccountservice.services.BankAccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BankAccountRestController {
    private final BankAccountService bankAccountService;

    public BankAccountRestController(BankAccountService bankAccountService){
        this.bankAccountService = bankAccountService;
    }

    @GetMapping("/bankAccounts")
    public List<BankAccountResponseDTO> showBankAccounts(){
        return bankAccountService.showBankAccounts();
    }

    @GetMapping("/bankAccounts/{id}")
    public BankAccountResponseDTO showBankAccount(@PathVariable String id){
        return bankAccountService.showBankAccount(id);
    }

    @PostMapping("/bankAccounts")
    public BankAccountResponseDTO addBankAccount(@RequestBody BankAccountRequestDTO bankAccountRequestDTO) {
        return bankAccountService.addBankAccount(bankAccountRequestDTO);
    }

    @PutMapping("/bankAccounts/{id}")
    public BankAccountResponseDTO updateBankAccount(@PathVariable String id,@RequestBody BankAccountRequestDTO bankAccountRequestDTO) {
        return bankAccountService.updateBankAccount(id, bankAccountRequestDTO);
    }

    @DeleteMapping("/bankAccounts/{id}")
    public String deleteBankAccount(@PathVariable String id){
        return bankAccountService.deleteBankAccount(id);
    }
}
