package com.ecom.customerservice.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecom.customerservice.dto.CustomersDTO;
import com.ecom.customerservice.dto.CustomersDTO.CustomerDTO;
import com.ecom.customerservice.entity.Customer;
import com.ecom.customerservice.mapper.CustomMapper;
import com.ecom.customerservice.repository.CustomerRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomerService {

	@Autowired
	CustomerRepository customerRepo;

	@Autowired
	CustomMapper cMapper;

	public CustomerDTO saveCustomer(CustomerDTO customer) throws Exception {
		// TODO Auto-generated method stub
      try {
		Customer cus =customerRepo.saveAndFlush(cMapper.toCustomerEntity(customer));
		return cMapper.toCustomerDTO(cus);
      }catch (Exception e) {
		// TODO: handle exception
    	  e.printStackTrace();
    	  throw e;
	}

	}

	public List<CustomersDTO.CustomerDTO> getUsers() throws Exception{
		// TODO Auto-generated method stub

		List<Customer> cus = customerRepo.findAll();

		return cMapper.toListCustomerDTO(cus);
	}

	public CustomerDTO findByid(UUID id) throws Exception{
		// TODO Auto-generated method stub
		Optional<Customer> customer = customerRepo.findById(id);
		return cMapper.toCustomerDTO(customer.get());
	}

}
