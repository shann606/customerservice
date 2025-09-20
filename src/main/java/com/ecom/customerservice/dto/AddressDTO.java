package com.ecom.customerservice.dto;

import java.util.List;
import java.util.UUID;

import com.ecom.customerservice.dto.CustomersDTO.CustomerDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {
	
	private UUID id;
	private UUID customerId;
	private String addr1;
	private String addr2;
	private String city;
	private String state;
	private String zipcode;
	private String country;

}
