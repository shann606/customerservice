package com.ecom.customerservice.service;

import static com.ecom.customerservice.util.AesEncryptionUtil.decrypt;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
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

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomerService {

	private CustomerRepository customerRepo;

	private CustomMapper cMapper;

	private CustomerServiceFeignClient feignClient;

	@Value("${crypto.secret-key}")
	private String secretKey;

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
		ObjectMapper obj;

		if (!secretKey.isBlank()) {
			obj = JsonMapper.builder().addModule(new JavaTimeModule()).build();
			String data = feignClient.getRecommendedProductsAsString(productItemId);
			log.info(" inner service is called as secure channel before decrypt" + data);
			if (data.startsWith("$")) {
				data = decrypt(data, secretKey);
			} else {
				throw new Exception("Downstream application is down please try later....");

			}
			log.info(" inner service is called as secure channel after decrypt" + data);

			result = obj.readValue(data, new TypeReference<List<CustomerRecommendationDTO.ProductsDTO>>() {
			});
		} else {
			log.info(" inner service is called as without secure channel");
			result = feignClient.getRecommendedProducts(productItemId);
		}
		return result;

	}

	/**
	 * Using Query by example method
	 * 
	 * @param name
	 * @param firstName
	 * @return
	 */
	public List<CustomersDTO.CustomerDTO> findByUserName(String name, String firstName) {

		log.info("Getting the name & firstname " + name + "--firstname--" + firstName);

		Customer probe = new Customer();
		probe.setName(name);
		probe.setFirtName(firstName);

		ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("name", match -> match.startsWith().ignoreCase())

				.withMatcher("firtName", match -> match.startsWith().ignoreCase());

		Example<Customer> example = Example.of(probe, matcher);

		List<Customer> cus = customerRepo.findAll(example);

		return cMapper.toListCustomerDTO(cus);
	}

	
	@Transactional
	public CustomerDTO updateCustomer(UUID customerId, CustomerDTO customer) throws Exception {

		customerRepo.findById(customerId).orElseThrow();
		customer.setId(customerId);

		Customer cust = cMapper.toCustomerEntity(customer);

		Customer cust1 = customerRepo.saveAndFlush(cust);

		return cMapper.toCustomerDTO(cust1);
	}

	@Transactional
	public CustomerDTO patchCustomerData(UUID customerId, CustomerDTO customer) throws Exception {

		Customer cust = customerRepo.findById(customerId).orElseThrow();

		// patching customer data

		if (customer.getName() != null) {
			cust.setName(customer.getName());
		}
		if (customer.getFirtName() != null) {
			cust.setFirtName(customer.getFirtName());
		}
		if (customer.getLastName() != null) {
			cust.setLastName(customer.getLastName());
		}
		if (customer.getAge() != null) {
			cust.setAge(customer.getAge());
		}
		if (customer.getGender() != null) {
			cust.setGender(customer.getGender());
		}
		// patching address data

		customer.getAddress().stream().forEach((s) -> {

			cust.getAddress().stream().filter(x -> x.getId().equals(s.getId())).forEach((y) -> {

				if (s.getAddr1() != null) {
					y.setAddr1(s.getAddr1());
				}
				if (s.getAddr2() != null) {
					y.setAddr2(s.getAddr2());
				}
				if (s.getCity() != null) {
					y.setCity(s.getCity());
				}
				if (s.getState() != null) {
					y.setState(s.getState());
				}
				if (s.getCountry() != null) {
					y.setCountry(s.getCountry());
				}
				if (s.getZipcode() != null) {
					y.setZipcode(s.getZipcode());
				}

			});

		});

		Customer cust1 = customerRepo.save(cust);

		return cMapper.toCustomerDTO(cust1);
	}

	public String deleteCustomer(UUID id) throws Exception {

		customerRepo.findById(id).orElseThrow();

		customerRepo.deleteById(id);

		return "Successfully deleted";
	}

}
