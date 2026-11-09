package org.sid.bankaccountservice.feign;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.sid.bankaccountservice.model.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//interface pour communiquer avec customer-service
//feign connait que le nom du microservice
//cest le discvery qui va lui donner laddresse de ce microservise

@FeignClient(name="customer-service")
public interface CustomerRestClient {

    @GetMapping("/customers/{id}")
    @CircuitBreaker(name="customerService",fallbackMethod = "getDefaultCustomer")
    Customer getCustomerById(@PathVariable Long id);

    default Customer getDefaultCustomer(Long id, Exception e){
        return new Customer(id,"not available","not available");
    }
}
