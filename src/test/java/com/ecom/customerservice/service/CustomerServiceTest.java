package com.ecom.customerservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import com.ecom.customerservice.dto.AddressDTO;
import com.ecom.customerservice.dto.CustomersDTO;
import com.ecom.customerservice.dto.CustomersDTO.CustomerDTO;
import com.ecom.customerservice.dto.GenderEnum;
import com.ecom.customerservice.entity.Address;
import com.ecom.customerservice.entity.Customer;
import com.ecom.customerservice.mapper.CustomMapper;
import com.ecom.customerservice.repository.CustomerRepository;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

	@Mock
	private CustomerRepository customerRepo;
	@Mock
	private CustomMapper cMapper;
	@Mock
	private CustomerServiceFeignClient feignClient;

	@InjectMocks
	private CustomerService customerService;

	private static Customer customer;

	private static CustomersDTO.CustomerDTO customerDTO;

	private static List<Customer> listCustomer;

	private static List<CustomersDTO.CustomerDTO> listCustomerDTO;

	@BeforeAll
	static void testInit() {
		List<Address> list = new ArrayList<Address>();
		List<AddressDTO> listDto = new ArrayList<AddressDTO>();
		listCustomer = new ArrayList<Customer>();
		listCustomerDTO = new ArrayList<CustomersDTO.CustomerDTO>();

		Address address = Address.builder().addr1("1445 Gillford Apt").addr2("kallis drive").city("Santa Clara")
				.country("USA").zipcode("95498").id(UUID.randomUUID())
				.customerId(UUID.fromString("b516f577-11da-424e-9ad0-bc23ab15df1b")).build();
		list.add(address);
		customer = Customer.builder().id(UUID.fromString("b516f577-11da-424e-9ad0-bc23ab15df1b")).firtName("David")
				.lastName("Mark").name("Peter").age(45).gender(GenderEnum.MALE).address(list).build();

		listCustomer.add(customer);

		// DTO

		AddressDTO address1 = AddressDTO.builder().addr1("1445 Gillford Apt").addr2("kallis drive").city("Santa Clara")
				.country("USA").zipcode("95498").id(UUID.randomUUID())
				.customerId(UUID.fromString("b516f577-11da-424e-9ad0-bc23ab15df1b")).build();
		listDto.add(address1);
		customerDTO = CustomerDTO.builder().id(UUID.fromString("b516f577-11da-424e-9ad0-bc23ab15df1b"))
				.firtName("David").lastName("Mark").name("Peter").age(45).gender(GenderEnum.MALE).address(listDto)
				.build();

		listCustomerDTO.add(customerDTO);
	}

	@BeforeEach
	void testConvert() {
		lenient().when(cMapper.toCustomerDTO(customer)).thenReturn(customerDTO);
		lenient().when(cMapper.toCustomerEntity(customerDTO)).thenReturn(customer);
		lenient().when(cMapper.toListCustomerDTO(listCustomer)).thenReturn(listCustomerDTO);

		lenient().when(customerRepo.findById(UUID.fromString("b516f577-11da-424e-9ad0-bc23ab15df1b")))
				.thenReturn(Optional.of(customer));

	}

	@Test
	void testSaveCustomer() {

		when(customerRepo.saveAndFlush(customer)).thenReturn(customer);

		CustomerDTO cusDTO = customerService.saveCustomer(customerDTO);

		assertEquals(UUID.fromString("b516f577-11da-424e-9ad0-bc23ab15df1b"), cusDTO.getId());
		assertNotNull(cusDTO);

		verify(customerRepo, times(1)).saveAndFlush(customer);

	}

	@Test
	void testGetUsers() {

		when(customerRepo.findAll(Sort.by("name").ascending())).thenReturn(listCustomer);

		List<CustomersDTO.CustomerDTO> result = customerService.getUsers();

		assertTrue(result.size() > 0);
		assertNotNull(result);

		verify(customerRepo, times(1)).findAll(Sort.by("name").ascending());

	}

	@Test
	void testFindByid() {

		when(customerRepo.findById(UUID.fromString("b516f577-11da-424e-9ad0-bc23ab15df1b")))
				.thenReturn(Optional.of(customer));

		CustomerDTO result = customerService.findByid(UUID.fromString("b516f577-11da-424e-9ad0-bc23ab15df1b"));
		assertEquals(UUID.fromString("b516f577-11da-424e-9ad0-bc23ab15df1b"), result.getId());
		assertNotNull(result);

		verify(customerRepo, times(1)).findById((UUID.fromString("b516f577-11da-424e-9ad0-bc23ab15df1b")));

	}



	@Test
	void testUpdateCustomer() throws Exception {

		when(customerRepo.saveAndFlush(customer)).thenReturn(customer);

		CustomerDTO result = customerService.updateCustomer(UUID.fromString("b516f577-11da-424e-9ad0-bc23ab15df1b"),
				customerDTO);

		assertEquals(UUID.fromString("b516f577-11da-424e-9ad0-bc23ab15df1b"), result.getId());
		assertNotNull(result);

		verify(customerRepo, times(1)).saveAndFlush(customer);

	}

	@Test
	void testPatchCustomerData() {

		when(customerRepo.save(customer)).thenReturn(customer);

		CustomerDTO result = customerService.patchCustomerData(UUID.fromString("b516f577-11da-424e-9ad0-bc23ab15df1b"),
				customerDTO);

		assertEquals(UUID.fromString("b516f577-11da-424e-9ad0-bc23ab15df1b"), result.getId());
		assertNotNull(result);

		verify(customerRepo, times(1)).save(customer);

	}

	@Test
	void testDeleteCustomer() {

		doNothing().when(customerRepo).deleteById(UUID.fromString("b516f577-11da-424e-9ad0-bc23ab15df1b"));

		customerService.deleteCustomer(UUID.fromString("b516f577-11da-424e-9ad0-bc23ab15df1b"));

		verify(customerRepo, times(1)).deleteById(UUID.fromString("b516f577-11da-424e-9ad0-bc23ab15df1b"));

	}

}
