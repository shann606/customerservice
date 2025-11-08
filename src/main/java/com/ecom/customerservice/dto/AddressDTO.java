package com.ecom.customerservice.dto;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4609616454860296130L;
	
	private UUID id;
	private UUID customerId;
	private String addr1;
	private String addr2;
	private String city;
	private String state;
	private String zipcode;
	private String country;

}
