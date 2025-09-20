package com.ecom.customerservice.dto;

import java.util.List;
import java.util.UUID;

import com.ecom.customerservice.util.GenderEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomersDTO {

	private List<CustomerDTO> customers;

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class CustomerDTO {

		private UUID id;
		private String name;
		private int age;
		private String firtName;
		private String lastName;
		private GenderEnum gender;
		private List<AddressDTO> address;

	}

}
