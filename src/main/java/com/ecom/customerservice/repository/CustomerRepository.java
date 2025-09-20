package com.ecom.customerservice.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.customerservice.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

}
