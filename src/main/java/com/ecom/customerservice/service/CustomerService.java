package com.ecom.customerservice.service;

import static com.ecom.customerservice.util.AesEncryptionUtil.decrypt;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.ecom.customerservice.dto.AddressDTO;
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

	public CustomerService(CustomerRepository customerRepo, CustomMapper cMapper,
			CustomerServiceFeignClient feignClient) {
		this.customerRepo = customerRepo;
		this.cMapper = cMapper;
		this.feignClient = feignClient;

	}

	public CustomerDTO saveCustomer(CustomerDTO customer) {

		try {

			Customer cus = customerRepo.saveAndFlush(cMapper.toCustomerEntity(customer));
			return cMapper.toCustomerDTO(cus);
		} catch (Exception e) {

			e.printStackTrace();
			throw e;
		}

	}

	@CacheEvict(cacheNames = "customers", allEntries = true)
	public List<CustomersDTO.CustomerDTO> getUsers() {

		log.info("Getting the users details");

		List<Customer> cus = customerRepo.findAll();

		return cMapper.toListCustomerDTO(cus);
	}

	@Cacheable(cacheNames = "customers", key = "#id", cacheManager = "cacheManager")
	public CustomerDTO findByid(UUID id) {

		log.info("are we hitting the database ::");
		Optional<Customer> customer = customerRepo.findById(id);
		return cMapper.toCustomerDTO(customer.orElseThrow());
	}

	public CustomerRecommendationDTO findCustomerProduct(UUID customerId, UUID productItemId) throws Exception {

		Customer customers = customerRepo.findById(customerId).orElseThrow(() -> new Exception("id not found"));

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
	@CachePut(cacheNames = "customers", key = "#customerId", cacheManager = "cacheManager")
	public CustomerDTO updateCustomer(UUID customerId, CustomerDTO customer) throws Exception {
		log.info("Update calling for customer");
		customerRepo.findById(customerId).orElseThrow();
		customer.setId(customerId);

		Customer cust = cMapper.toCustomerEntity(customer);

		Customer cust1 = customerRepo.saveAndFlush(cust);

		return cMapper.toCustomerDTO(cust1);
	}

	@Transactional
	public CustomerDTO patchCustomerData(UUID customerId, CustomerDTO customer) {

		Customer cust = customerRepo.findById(customerId).orElseThrow();

		// patching customer data

		setValuesifPresent(customer.getName(), cust::setName);
		setValuesifPresent(customer.getFirtName(), cust::setFirtName);
		setValuesifPresent(customer.getLastName(), cust::setLastName);
		setValuesifPresent(customer.getAge(), cust::setAge);
		setValuesifPresent(customer.getGender(), cust::setGender);

		// patching address data
		if (!customer.getAddress().isEmpty()) {

			customer.getAddress().stream().forEach(s -> {

				cust.getAddress().stream().filter(x -> x.getId().equals(s.getId())).forEach(y -> {

					setValuesifPresent(s.getAddr1(), y::setAddr1);
					setValuesifPresent(s.getAddr2(), y::setAddr2);
					setValuesifPresent(s.getCity(), y::setCity);
					setValuesifPresent(s.getState(), y::setState);
					setValuesifPresent(s.getCountry(), y::setCountry);
					setValuesifPresent(s.getZipcode(), y::setZipcode);

				});

			});
		}

		Customer cust1 = customerRepo.save(cust);

		return cMapper.toCustomerDTO(cust1);
	}

	public String deleteCustomer(UUID id) {

		customerRepo.findById(id).orElseThrow();

		customerRepo.deleteById(id);

		return "Successfully deleted";
	}

	private <T> void setValuesifPresent(T value, Consumer<T> setter) {

		if (value != null) {
			setter.accept(value);
		}

	}

}
