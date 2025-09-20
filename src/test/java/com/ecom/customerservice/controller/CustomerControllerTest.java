package com.ecom.customerservice.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ecom.customerservice.service.CustomerService;

@SpringBootTest
class CustomerControllerTest {
	
	@Autowired
	private MockMvc mockMVC;
	
	@MockBean
	private CustomerService customerService;
	
	
	
	
	@BeforeAll
	void setData() {
		
	}
	

	@Test
	void testSaveCustomer() {
		fail("Not yet implemented");
	}

	@Test
	void testGetCustomers() {
		fail("Not yet implemented");
	}

	@Test
	void testGetUser() {
		fail("Not yet implemented");
	}

}
