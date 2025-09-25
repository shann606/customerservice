package com.ecom.customerservice.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.ecom.customerservice.util.GenderEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = { "age", "firtName", "lastName", "gender", "address" }, ignoreUnknown = true)
public class CustomerRecommendationDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7044213396291688598L;

	private UUID id;
	private String name;

	private int age;
	private String firtName;
	private String lastName;
	private GenderEnum gender;
	private List<AddressDTO> address;
	
	private List<CustomerRecommendationDTO.ProductsDTO> prods;
	
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ProductsDTO implements Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -9043196216381167018L;
		private UUID id;
		private UUID productItemId;
		private String brand;
		private String description;
		private BigDecimal price;
		private boolean available;
		@JsonIgnore
		private OffsetDateTime createdOn;
		@JsonIgnore
		private OffsetDateTime updatedOn;
		@JsonIgnore
		private String createdBy;
		@JsonIgnore
		private String updatedBy;
		
		
	}

}
