package com.ecom.customerservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Description;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.ecom.customerservice.dto.AddressDTO;
import com.ecom.customerservice.dto.CustomersDTO;
import com.ecom.customerservice.service.CustomerService;
import com.ecom.customerservice.util.GenderEnum;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CustomerControllerTest {

	@Autowired
	private MockMvc mockMVC;

	@MockBean
	private CustomerService customerService;

	private static CustomersDTO.CustomerDTO customer;

	private static List<CustomersDTO.CustomerDTO> cus = new ArrayList<CustomersDTO.CustomerDTO>();

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeAll
	static void setData() {
		List<AddressDTO> list = new ArrayList<AddressDTO>();

		AddressDTO address = AddressDTO.builder().addr1("1445 Gillford Apt").addr2("kallis drive").city("Santa Clara")
				.country("USA").zipcode("95498").id(UUID.randomUUID()).build();
		list.add(address);
		customer = CustomersDTO.CustomerDTO.builder().id(UUID.fromString("b516f577-11da-424e-9ad0-bc23ab15df1b"))
				.firtName("David").lastName("Mark").name("Peter").age(45).gender(GenderEnum.Male).address(list).build();
		cus.add(customer);

	}

	@Test
	@Description("Customer : Saving customer testing")
	void testSaveCustomer() throws Exception {
		when(customerService.saveCustomer(customer)).thenReturn(customer);
		
		mockMVC.perform(post("/api/v1/customers/createcustomer").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(customer))).andExpect(status().isCreated()).andReturn();
		
			verify(customerService, atLeastOnce()).saveCustomer(customer);
		
		

	}

	@Test
	@Description("Customer : Getting all the customers")
	void testGetCustomers() throws Exception {
		when(customerService.getUsers()).thenReturn(cus);
		mockMVC.perform(get("/api/v1/customers/allcustomers").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		verify(customerService, atLeastOnce()).getUsers();

	}

	@Test
	@Description("Customer : Getting a user by passing id")
	void testGetUser() throws Exception {

		UUID id = UUID.fromString("b516f577-11da-424e-9ad0-bc23ab15df1b");
		when(customerService.findByid(id)).thenReturn(customer);

		mockMVC.perform(get("/api/v1/customers/{id}", id).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		verify(customerService, atLeastOnce()).findByid(id);
	}

}
