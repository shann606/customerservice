/**
 * 
 */
package com.ecom.customerservice.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ecom.customerservice.dto.AddressDTO;
import com.ecom.customerservice.dto.CustomersDTO;
import com.ecom.customerservice.dto.GenderEnum;
import com.ecom.customerservice.dto.CustomersDTO.CustomerDTO;
import com.ecom.customerservice.dto.CustomerRecommendationDTO;
import com.ecom.customerservice.service.CustomerService;
import com.ecom.customerservice.util.AesEncryptionUtil;

/**
 * Shan
 */

@RestController
@RequestMapping("/api/v1/customers")

class CustomerController {

	CustomerService customerService;

	@Autowired
	public CustomerController(CustomerService customerService) {
		this.customerService = customerService;
	}

	@PostMapping("/createcustomer")
	ResponseEntity<CustomersDTO.CustomerDTO> saveCustomer(@RequestBody CustomersDTO.CustomerDTO customer)
			throws Exception {

		CustomersDTO.CustomerDTO customer1 = customerService.saveCustomer(customer);

		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/customers/{id}")
				.buildAndExpand(customer1.getId()).toUri();

		return ResponseEntity.created(location).body(customer1);

	}

	@GetMapping("/allcustomers")
	ResponseEntity<CustomersDTO> getCustomers() throws Exception {

		List<CustomerDTO> customer = customerService.getUsers();
		System.out.println("printing key =="+ AesEncryptionUtil.generateKey());

		return ResponseEntity.ok(CustomersDTO.builder().customers(customer).build());
	}

	@GetMapping("/{id}")
	ResponseEntity<CustomersDTO.CustomerDTO> getUser(@PathVariable("id") UUID id) throws Exception {

		CustomersDTO.CustomerDTO customer = customerService.findByid(id);
		return ResponseEntity.ok(customer);

	}
	
	
	@GetMapping("/getcustomer")
	ResponseEntity<CustomersDTO> findUser(@RequestParam(name = "name", required = true)  String name,
			          								  @RequestParam(name = "firstname", required = true) String firstName) throws Exception {
		
		List<CustomerDTO> customer =customerService.findByUserName(name,firstName);
		return ResponseEntity.ok(CustomersDTO.builder().customers(customer).build());
		 
	 }
	

	@GetMapping("/getrecommendations")
	ResponseEntity<CustomerRecommendationDTO> getRecommendations(
			@RequestParam(name = "customerId", required = true) UUID customerId,
			@RequestParam(name = "productItemid", required = true) UUID productItemId) throws Exception {

		return ResponseEntity.ok(customerService.findCustomerProduct(customerId, productItemId));

	}

	private CustomersDTO getData() {

		List<AddressDTO> list = new ArrayList<>();
		List<CustomersDTO.CustomerDTO> cus = new ArrayList<>();
		AddressDTO address = AddressDTO.builder().addr1("1445 Gillford Apt").addr2("kallis drive").city("Santa Clara")
				.country("USA").zipcode("95498").id(UUID.randomUUID()).build();
		list.add(address);
		CustomersDTO.CustomerDTO customer = CustomersDTO.CustomerDTO.builder().id(UUID.randomUUID()).firtName("David")
				.lastName("Mark").name("Peter").age(45).gender(GenderEnum.MALE).address(list).build();
		cus.add(customer);

		return CustomersDTO.builder().customers(cus).build();

	}
}
