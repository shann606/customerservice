/**
 * 
 */
package com.ecom.customerservice.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ecom.customerservice.dto.AddressDTO;
import com.ecom.customerservice.dto.CustomerRecommendationDTO;
import com.ecom.customerservice.dto.CustomersDTO;
import com.ecom.customerservice.dto.CustomersDTO.CustomerDTO;
import com.ecom.customerservice.dto.GenderEnum;
import com.ecom.customerservice.dto.ValidationDTO;
import com.ecom.customerservice.service.CustomerService;
import com.ecom.customerservice.validate.CustomerValidator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

/**
 * Shan
 */

@RestController
@RequestMapping("/api/v1/customers")

class CustomerController {

	CustomerService customerService;
	CustomerValidator customerValidator;

	public CustomerController(CustomerService customerService, CustomerValidator customerValidator) {
		this.customerService = customerService;
		this.customerValidator = customerValidator;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(customerValidator);
	}

	@PostMapping("/createcustomer")
	@Operation(description = "Creating Customer", responses = {
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "200", description = "Success")

	})
	ResponseEntity<Object> saveCustomer(@Valid @RequestBody CustomersDTO.CustomerDTO customer, BindingResult result)
			 {
		

		if (result.hasErrors()) {
			ValidationDTO res = null;

			for (ObjectError s : result.getAllErrors()) {

				res = new ValidationDTO("Failed", s.getCode(), s.getDefaultMessage());

			}
			return ResponseEntity.badRequest().body(res);

		}

		CustomersDTO.CustomerDTO customer1 = customerService.saveCustomer(customer);

		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/customers/{id}")
				.buildAndExpand(customer1.getId()).toUri();

		return ResponseEntity.created(location).body(customer1);

	}

	@GetMapping("/allcustomers")
	@Operation(description = "Getting all the customers", responses = {
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "200", description = "Success")

	})
	ResponseEntity<CustomersDTO> getCustomers()  {

		List<CustomerDTO> customer = customerService.getUsers();

		return ResponseEntity.ok(CustomersDTO.builder().customers(customer).build());
	}

	@GetMapping("/{id}")
	@Operation(description = "Getting single customer by id", responses = {
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "200", description = "Success")

	})
	ResponseEntity<CustomersDTO.CustomerDTO> getUser(@PathVariable("id") UUID id)  {

		CustomersDTO.CustomerDTO customer = customerService.findByid(id);
		return ResponseEntity.ok(customer);

	}

	@GetMapping("/getcustomer")
	@Operation(description = "Getting customer by name", responses = {
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "200", description = "Success")

	})
	ResponseEntity<CustomersDTO> findUser(@RequestParam(name = "name", required = true) String name,
			@RequestParam(name = "firstname", required = true) String firstName)  {

		List<CustomerDTO> customer = customerService.findByUserName(name, firstName);
		return ResponseEntity.ok(CustomersDTO.builder().customers(customer).build());

	}

	@GetMapping("/getrecommendations")
	@Operation(description = "Getting recommendation of products for the customer", responses = {
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "200", description = "Success")

	})
	ResponseEntity<CustomerRecommendationDTO> getRecommendations(
			@RequestParam(name = "customerId", required = true) UUID customerId,
			@RequestParam(name = "productItemid", required = true) UUID productItemId) throws Exception  {

		return ResponseEntity.ok(customerService.findCustomerProduct(customerId, productItemId));

	}

	@PutMapping("/updatecustomer")
	@Operation(description = "Updating a customer", responses = {
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "200", description = "Success")

	})
	ResponseEntity<CustomerDTO> updateCustomer(@RequestParam(name = "id", required = true) UUID customerId,
			@RequestBody CustomerDTO customer) throws Exception {

		return new ResponseEntity<>(customerService.updateCustomer(customerId, customer), HttpStatus.ACCEPTED);

	}

	@PatchMapping("/patchcustomer")
	@Operation(description = "Patching customer", responses = {
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "200", description = "Success")

	})
	ResponseEntity<CustomerDTO> patchCustomer(@RequestParam(name = "id", required = true) UUID customerId,
			@RequestBody CustomerDTO customer)  {

		return new ResponseEntity<>(customerService.patchCustomerData(customerId, customer), HttpStatus.ACCEPTED);
	}

	@DeleteMapping("/deletecustomer/{id}")

	@Operation(description = "Deleting a Customer", responses = {
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "200", description = "Success")

	})
	ResponseEntity<String> deleteCustomer(@PathVariable(name = "id", required = true) UUID id)  {

		return new ResponseEntity<>(customerService.deleteCustomer(id), HttpStatus.NO_CONTENT);
	}
     
	@SuppressWarnings("unused")
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
