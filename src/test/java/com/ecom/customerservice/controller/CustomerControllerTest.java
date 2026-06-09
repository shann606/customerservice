package com.ecom.customerservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;

import com.ecom.customerservice.dto.AddressDTO;
import com.ecom.customerservice.dto.CustomersDTO;
import com.ecom.customerservice.dto.CustomersDTO.CustomerDTO;
import com.ecom.customerservice.dto.GenderEnum;
import com.ecom.customerservice.service.CustomerService;
import com.ecom.customerservice.validate.CustomerValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest
class CustomerControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private BindingResult result;

	@MockitoBean
	private CustomerValidator customerValidator;

	@Autowired
	private ObjectMapper objMapper;

	@MockitoBean
	private CustomerService customerService;

	private static CustomersDTO.CustomerDTO customer;

	private static List<CustomersDTO.CustomerDTO> cus = new ArrayList<CustomersDTO.CustomerDTO>();

	@BeforeAll
	static void init() {
		List<AddressDTO> list = new ArrayList<AddressDTO>();

		AddressDTO address = AddressDTO.builder().addr1("1445 Gillford Apt").addr2("kallis drive").city("Santa Clara")
				.country("USA").zipcode("95498").id(UUID.randomUUID()).build();
		list.add(address);
		customer = CustomerDTO.builder().id(UUID.fromString("b516f577-11da-424e-9ad0-bc23ab15df1b")).firtName("David")
				.lastName("Mark").name("Peter").age(45).gender(GenderEnum.MALE).address(list).build();
		cus.add(customer);

	}

	@Test
	void testSaveCustomer() throws Exception {

		when(customerService.saveCustomer(any(CustomersDTO.CustomerDTO.class))).thenReturn(customer);
		when(result.hasErrors()).thenReturn(false);
		when(customerValidator.supports(CustomersDTO.CustomerDTO.class)).thenReturn(true);

		mockMvc.perform(post("/api/v1/customers/createcustomer").contentType(MediaType.APPLICATION_JSON)
				.content(objMapper.writeValueAsBytes(customer))).andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.name").value("Peter"));

		verify(customerService, times(1)).saveCustomer(any(CustomersDTO.CustomerDTO.class));

	}

	@Test
	void testGetCustomers() throws Exception {

		when(customerService.getUsers()).thenReturn(cus);

		mockMvc.perform(get("/api/v1/customers/allcustomers")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

		verify(customerService, times(1)).getUsers();

	}

	@Test
	void testGetUser() throws Exception {

		when(customerService.findByid(any(UUID.class))).thenReturn(customer);

		mockMvc.perform(get("/api/v1/customers/{id}", UUID.fromString("b516f577-11da-424e-9ad0-bc23ab15df1b")))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.name").value("Peter"));

		verify(customerService, times(1)).findByid(any(UUID.class));

	}

	@Test
	void testFindUser() throws Exception {

		when(customerService.findByUserName(any(String.class), any(String.class))).thenReturn(cus);

		mockMvc.perform(get("/api/v1/customers/getcustomer").param("name", "Peter").param("firstname", "David"))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

		verify(customerService, times(1)).findByUserName(any(String.class), any(String.class));

	}

	@Test
	void testUpdateCustomer() throws Exception {

		when(customerService.updateCustomer(any(UUID.class), any(CustomersDTO.CustomerDTO.class))).thenReturn(customer);
		when(customerValidator.supports(CustomersDTO.CustomerDTO.class)).thenReturn(true);

		mockMvc.perform(put("/api/v1/customers/updatecustomer").param("id", "b516f577-11da-424e-9ad0-bc23ab15df1b")
				.contentType(MediaType.APPLICATION_JSON).content(objMapper.writeValueAsString(customer)))
				.andExpect(status().isAccepted()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.name").value("Peter"));

		verify(customerService, times(1)).updateCustomer(any(UUID.class), any(CustomersDTO.CustomerDTO.class));

	}

	@Test
	void testPatchCustomer() throws JsonProcessingException, Exception {

		when(customerService.patchCustomerData(any(UUID.class), any(CustomersDTO.CustomerDTO.class)))
				.thenReturn(customer);
		when(customerValidator.supports(CustomersDTO.CustomerDTO.class)).thenReturn(true);

		mockMvc.perform(patch("/api/v1/customers/patchcustomer").param("id", "b516f577-11da-424e-9ad0-bc23ab15df1b")
				.contentType(MediaType.APPLICATION_JSON).content(objMapper.writeValueAsString(customer)))
				.andExpect(status().isAccepted()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.name").value("Peter"));

		verify(customerService, times(1)).patchCustomerData(any(UUID.class), any(CustomersDTO.CustomerDTO.class));

	}

	@Test
	void testDeleteCustomer() throws Exception {

		when(customerService.deleteCustomer(any(UUID.class))).thenReturn(null);

		mockMvc.perform(delete("/api/v1/customers//deletecustomer/{id}",
				UUID.fromString("b516f577-11da-424e-9ad0-bc23ab15df1b")))
				.andExpect(status().isNoContent());
		
		verify(customerService, times(1)).deleteCustomer(any(UUID.class));

	}

}
