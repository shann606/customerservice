package com.ecom.customerservice.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecom.customerservice.dto.CustomerRecommendationDTO;
import com.ecom.customerservice.dto.CustomersDTO;
import com.ecom.customerservice.dto.CustomersDTO.CustomerDTO;
import com.ecom.customerservice.entity.Customer;
import com.ecom.customerservice.mapper.CustomMapper;
import com.ecom.customerservice.repository.CustomerRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomerService {

	private CustomerRepository customerRepo;

	private CustomMapper cMapper;

	private CustomerServiceFeignClient feignClient;

	@Autowired
	public CustomerService(CustomerRepository customerRepo, CustomMapper cMapper,
			CustomerServiceFeignClient feignClient) {
		this.customerRepo = customerRepo;
		this.cMapper = cMapper;
		this.feignClient = feignClient;

	}

	public CustomerDTO saveCustomer(CustomerDTO customer) throws Exception {

		try {
			Customer cus = customerRepo.saveAndFlush(cMapper.toCustomerEntity(customer));
			return cMapper.toCustomerDTO(cus);
		} catch (Exception e) {

			e.printStackTrace();
			throw e;
		}

	}

	public List<CustomersDTO.CustomerDTO> getUsers() throws Exception {

		List<Customer> cus = customerRepo.findAll();

		return cMapper.toListCustomerDTO(cus);
	}

	public CustomerDTO findByid(UUID id) throws Exception {

		Optional<Customer> customer = customerRepo.findById(id);
		return cMapper.toCustomerDTO(customer.orElseThrow());
	}

	public CustomerRecommendationDTO findCustomerProduct(UUID customerId, UUID productItemId) throws Exception {

		Customer customers = customerRepo.findById(customerId).orElseThrow();

		CustomerRecommendationDTO cust = cMapper.toCustomerRecommendationDTO(customers);

		List<CustomerRecommendationDTO.ProductsDTO> details = getProductsData(productItemId);
		cust.setProds(details);

		return cust;
	}

	private List<CustomerRecommendationDTO.ProductsDTO> getProductsData(UUID productItemId) throws Exception {

		return feignClient.getRecommendedProducts(productItemId);

	}

}
