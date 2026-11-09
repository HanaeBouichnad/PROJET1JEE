package org.sid.customerservice.services;

import org.sid.customerservice.dtos.CustomerRequestDTO;
import org.sid.customerservice.dtos.CustomerResponseDTO;
import org.sid.customerservice.entities.Customer;
import org.sid.customerservice.mappers.CustomerMapper;
import org.sid.customerservice.repositories.CustomerRepository;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository=customerRepository;
        this.customerMapper=customerMapper;
    }

    @McpTool(description = "Show all customers")
    public List<CustomerResponseDTO> showCustomers(){
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::toCustomerResponseDTO)
                .toList();
    }

    @McpTool(description = "Show one customer by his id")
    public CustomerResponseDTO showCustomer(@McpToolParam(description="The id of the customer to show") Long id){
        Customer customer=customerRepository.findById(id).orElseThrow(()->new RuntimeException("Customer not found!"));
        return customerMapper.toCustomerResponseDTO(customer);
    }

    @McpTool(description = "Add a new customer")
    public CustomerResponseDTO addCustomer(@McpToolParam(description = "The customer to add") CustomerRequestDTO customerRequestDTO){
        Customer customerToAdd=customerMapper.toCustomer(customerRequestDTO);
        Customer customerAdded=customerRepository.save(customerToAdd);
        return customerMapper.toCustomerResponseDTO(customerAdded);
    }

    @McpTool(description = "Update a customer")
    public CustomerResponseDTO updateCustomer(
            @McpToolParam(description="The id of the customer to update") Long id,
            @McpToolParam(description="The customer with the new values of the fields to update") CustomerRequestDTO customerRequestDTO){
        Customer customerToUpdate=customerRepository.findById(id).orElseThrow(()->new RuntimeException("Customer not found!"));
        Customer customer=customerMapper.toCustomer(customerRequestDTO);
        if(customer.getName()!=null) customerToUpdate.setName(customer.getName());
        if(customer.getEmail()!=null) customerToUpdate.setEmail(customer.getEmail());
        Customer customerUpdated=customerRepository.save(customerToUpdate);
        return customerMapper.toCustomerResponseDTO(customerUpdated);
    }

    @McpTool(description = "Delete a customer by his id")
    public String deleteCustomer(@McpToolParam(description = "The id of the customer to delete") Long id){
        Customer customer=customerRepository.findById(id).orElseThrow(()->new RuntimeException("Customer not found!"));
        customerRepository.delete(customer);
        return "Customer with id "+id+" has been deleted";
    }
}
