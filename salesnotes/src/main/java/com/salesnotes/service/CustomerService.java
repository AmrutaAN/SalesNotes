package com.salesnotes.service;

import com.salesnotes.dto.CustomerDto;
import com.salesnotes.entity.Customer;
import com.salesnotes.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<CustomerDto> getAllCustomers(String search) {
        List<Customer> list;
        if (search != null && !search.trim().isEmpty()) {
            list = customerRepository.findByNameContainingIgnoreCaseOrCompanyContainingIgnoreCase(search.trim(), search.trim());
        } else {
            list = customerRepository.findAll();
        }
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    public Optional<CustomerDto> getCustomerById(Long id) {
        return customerRepository.findById(id).map(this::toDto);
    }

    public CustomerDto createCustomer(CustomerDto dto) {
        Customer customer = new Customer(dto.getName(), dto.getCompany(), dto.getEmail(), dto.getPhone(), dto.getIndustry());
        Customer saved = customerRepository.save(customer);
        return toDto(saved);
    }

    public Optional<CustomerDto> updateCustomer(Long id, CustomerDto dto) {
        return customerRepository.findById(id).map(existing -> {
            existing.setName(dto.getName());
            existing.setCompany(dto.getCompany());
            existing.setEmail(dto.getEmail());
            existing.setPhone(dto.getPhone());
            existing.setIndustry(dto.getIndustry());
            return toDto(customerRepository.save(existing));
        });
    }

    public boolean deleteCustomer(Long id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private CustomerDto toDto(Customer c) {
        return new CustomerDto(c.getId(), c.getName(), c.getCompany(), c.getEmail(), c.getPhone(), c.getIndustry());
    }
}
