package com.ecom.customerservice.dto;

import java.util.List;

import com.ecom.customerservice.dto.CustomersDTO.CustomerDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionDTO {
	
	private String status;
	private String exception;

}
