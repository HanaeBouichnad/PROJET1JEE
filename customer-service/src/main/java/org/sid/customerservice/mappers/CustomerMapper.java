package org.sid.customerservice.mappers;

import org.sid.customerservice.dtos.CustomerRequestDTO;
import org.sid.customerservice.dtos.CustomerResponseDTO;
import org.sid.customerservice.entities.Customer;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {
    public CustomerResponseDTO toCustomerResponseDTO(Customer customer){
        CustomerResponseDTO customerResponseDTO=new CustomerResponseDTO();
        BeanUtils.copyProperties(customer,customerResponseDTO);
        return customerResponseDTO;
    }
    public Customer toCustomer(CustomerRequestDTO customerRequestDTO){
        Customer customer=new Customer();
        BeanUtils.copyProperties(customerRequestDTO,customer);
        return customer;
    }
}
