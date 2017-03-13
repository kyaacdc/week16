package com.smarthouse.repository;

import com.smarthouse.pojo.Customer;
import org.springframework.data.repository.Repository;

public interface CustomerDao extends Repository<Customer, String> {
    Customer save(Customer customer);
    Boolean exists(String email);
    Customer findByEmail(String email);
}
