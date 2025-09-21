package com.ecom.customerservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;

import com.ecom.customerservice.dto.AddressDTO;
import com.ecom.customerservice.dto.CustomersDTO;
import com.ecom.customerservice.entity.Address;
import com.ecom.customerservice.entity.Customer;
import com.ecom.customerservice.mapper.CustomMapper;
import com.ecom.customerservice.repository.CustomerRepository;
import com.ecom.customerservice.util.GenderEnum;


@ExtendWith(MockitoExtension.class)
@SpringBootTest
class CustomerServiceTest {
	
	@Mock
	private CustomerRepository customerRepo;
	
	@Spy
	private CustomMapper cMapper =Mappers.getMapper(CustomMapper.class);;
	
	@InjectMocks
	private CustomerService customerService;
	
	private static Customer customer;



	
	
	
	@BeforeAll
	static void setData() {
		List<Address> list = new ArrayList<Address>();

		Address address = Address.builder().addr1("1445 Gillford Apt").addr2("kallis drive").city("Santa Clara")
				.country("USA").zipcode("95498").id(UUID.randomUUID()).customerId(UUID.fromString("b516f577-11da-424e-9ad0-bc23ab15df1b")).build();
	  list.add(address);
		customer = Customer.builder().id(UUID.fromString("b516f577-11da-424e-9ad0-bc23ab15df1b"))
				.firtName("David").lastName("Mark").name("Peter").age(45).gender(GenderEnum.Male).address(list).build();
	
	}
	
	@BeforeEach
	void setUp() {
		lenient().when(customerRepo.saveAndFlush(customer)).thenReturn(customer);
	}
	
	@BeforeEach
	void setUpById() {
		lenient().when(customerRepo.findById(UUID.fromString("b516f577-11da-424e-9ad0-bc23ab15df1b"))).thenReturn(Optional.of(customer));
	}

	@Test
	@Description("Customer : Saving customer testing")
	void testSaveCustomer() throws Exception {
		
		 Customer c=	cMapper.toCustomerEntity(customerService.saveCustomer(cMapper.toCustomerDTO(customer)));
		 verify(customerRepo, atLeastOnce()).saveAndFlush(customer);
	
	}


	@Test
	@Description("Customer : getting customer by id ")
	void testFindByid() throws Exception {
		 Customer c=	cMapper.toCustomerEntity(customerService.findByid((UUID.fromString("b516f577-11da-424e-9ad0-bc23ab15df1b"))));
		 verify(customerRepo, atLeastOnce()).findById(UUID.fromString("b516f577-11da-424e-9ad0-bc23ab15df1b"));
	}

}
