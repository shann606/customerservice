package com.ecom.customerservice.service;

import static com.ecom.customerservice.util.AesEncryptionUtil.decrypt;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ecom.customerservice.dto.CustomerRecommendationDTO;
import com.ecom.customerservice.dto.CustomersDTO;
import com.ecom.customerservice.dto.CustomersDTO.CustomerDTO;
import com.ecom.customerservice.entity.Customer;
import com.ecom.customerservice.mapper.CustomMapper;
import com.ecom.customerservice.repository.CustomerRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomerService {

	private CustomerRepository customerRepo;

	private CustomMapper cMapper;

	private CustomerServiceFeignClient feignClient;

	@Value("${crypto.secret-key}")
	private String secretKey;

	private ObjectMapper obj;

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

		log.info("Getting the users details");		
		
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

		List<CustomerRecommendationDTO.ProductsDTO> result;

		if (!secretKey.isBlank()) {

			String data = feignClient.getRecommendedProductsAsString(productItemId);
			log.info(" inner service is called as secure channel before decrypt" + data);

			data = decrypt(data, secretKey);

			log.info(" inner service is called as secure channel after decrypt" + data);

			obj = JsonMapper.builder().addModule(new JavaTimeModule()).build();

			result = obj.readValue(data, new TypeReference<List<CustomerRecommendationDTO.ProductsDTO>>() {
			});
		} else {
			log.info(" inner service is called as without secure channel");
			result = feignClient.getRecommendedProducts(productItemId);
		}
		return result;

	}

}
