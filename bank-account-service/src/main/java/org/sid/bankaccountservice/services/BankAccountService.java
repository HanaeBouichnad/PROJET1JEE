package org.sid.bankaccountservice.services;

import org.sid.bankaccountservice.dtos.BankAccountRequestDTO;
import org.sid.bankaccountservice.dtos.BankAccountResponseDTO;
import org.sid.bankaccountservice.entities.BankAccount;
import org.sid.bankaccountservice.feign.CustomerRestClient;
import org.sid.bankaccountservice.mappers.BankAccountMapper;
import org.sid.bankaccountservice.model.Customer;
import org.sid.bankaccountservice.repositories.BankAccountRepository;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final BankAccountMapper bankAccountMapper;
    private final CustomerRestClient customerRestClient;

    public BankAccountService(BankAccountRepository bankAccountRepository, BankAccountMapper bankAccountMapper, CustomerRestClient customerRestClient) {
        this.bankAccountRepository = bankAccountRepository;
        this.bankAccountMapper = bankAccountMapper;
        this.customerRestClient = customerRestClient;
    }

    @McpTool(description = "Show all bank accounts")
    public List<BankAccountResponseDTO> showBankAccounts(){
        return bankAccountRepository.findAll()
                .stream()
                .map(bankAccountMapper::toBankAccountResponseDTO)
                .toList();
    }

    @McpTool(description = "Show a bank account by its id")
    public BankAccountResponseDTO showBankAccount(@McpToolParam(description = "The id of the bank account to show") String id){
        BankAccount bankAccount=bankAccountRepository.findById(id).orElseThrow(()->new RuntimeException("Bank account not found!"));
        Customer customer= customerRestClient.getCustomerById(bankAccount.getCustomerId());
        bankAccount.setCustomer(customer);
        return bankAccountMapper.toBankAccountResponseDTO(bankAccount);
    }

    @McpTool(description = "Add a bank account for an existing customer")
    public BankAccountResponseDTO addBankAccount(@McpToolParam(description = "The bank account to add") BankAccountRequestDTO bankAccountRequestDTO){
        Customer customer;
        try{
            customer=customerRestClient.getCustomerById(bankAccountRequestDTO.getCustomerId()); //customerId quila donné doit appartenir à un customer
        }
        catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
        BankAccount bankAccountToAdd=bankAccountMapper.toBankAccount(bankAccountRequestDTO); //contient que balance,type,customerId
        bankAccountToAdd.setId(UUID.randomUUID().toString());
        bankAccountToAdd.setCreatedAt( new Date() );
        BankAccount bankAccountAdded=bankAccountRepository.save(bankAccountToAdd);
        bankAccountAdded.setCustomer(customer);
        return bankAccountMapper.toBankAccountResponseDTO(bankAccountAdded);
    }

    @McpTool(description = "Update a bank account by its id")
    public BankAccountResponseDTO updateBankAccount(
            @McpToolParam(description = "The id of the bank account to update") String id,
            @McpToolParam(description="The bank account with new values of the fields to update") BankAccountRequestDTO bankAccountRequestDTO){
        BankAccount bankAccountToUpdate=bankAccountRepository.findById(id).orElseThrow(()->new RuntimeException("Bank account not found!"));
        BankAccount bankAccount=bankAccountMapper.toBankAccount(bankAccountRequestDTO);
        if(bankAccount.getBalance()!=null) bankAccountToUpdate.setBalance(bankAccount.getBalance());
        if(bankAccount.getType()!=null) bankAccountToUpdate.setType(bankAccount.getType());
        if(bankAccount.getCustomerId()!=null) bankAccountToUpdate.setCustomerId(bankAccount.getCustomerId());
        BankAccount bankAccountUpdated=bankAccountRepository.save(bankAccountToUpdate);
        return bankAccountMapper.toBankAccountResponseDTO(bankAccountUpdated);
    }

    @McpTool(description = "Delete a bank account by its id")
    public String deleteBankAccount(@McpToolParam(description = "The id of the bank account to delete") String id){
        BankAccount bankAccountToDelete=bankAccountRepository.findById(id).orElseThrow(()->new RuntimeException("Bank account not found!"));
        bankAccountRepository.delete(bankAccountToDelete);
        return "Bank account with id "+id+" has been deleted";
    }
}
