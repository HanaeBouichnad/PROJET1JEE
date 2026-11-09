package org.sid.customerservice.controllers;

import org.sid.customerservice.dtos.CustomerRequestDTO;
import org.sid.customerservice.dtos.CustomerResponseDTO;
import org.sid.customerservice.services.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CustomerRestController {
    private final CustomerService customerService;

    public CustomerRestController(CustomerService customerService) {
        this.customerService=customerService;
    }

    @GetMapping("/customers")
    public List<CustomerResponseDTO> showCustomers(){
        return customerService.showCustomers();
    }

    @GetMapping("/customers/{id}")
    public CustomerResponseDTO showCustomer(@PathVariable Long id){
        return customerService.showCustomer(id);
    }

    @PostMapping("/customers")
    public CustomerResponseDTO addCustomer(@RequestBody CustomerRequestDTO customerRequestDTO){
        return customerService.addCustomer(customerRequestDTO);
    }

    @PutMapping("/customers/{id}")
    public CustomerResponseDTO updateCustomer(@PathVariable Long id,@RequestBody CustomerRequestDTO customerRequestDTO){
        return customerService.updateCustomer(id,customerRequestDTO);
    }

    @DeleteMapping("/customers/{id}")
    public String deleteCustomer(@PathVariable Long id){
        return customerService.deleteCustomer(id);
    }
}
