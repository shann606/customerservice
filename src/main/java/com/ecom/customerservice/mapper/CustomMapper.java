package com.ecom.customerservice.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.ecom.customerservice.dto.CustomersDTO;
import com.ecom.customerservice.dto.CustomersDTO.CustomerDTO;
import com.ecom.customerservice.dto.CustomerRecommendationDTO;
import com.ecom.customerservice.entity.Customer;

@Mapper(componentModel = "spring")
public interface CustomMapper {
	
	CustomerDTO toCustomerDTO(Customer customer);
	
	Customer toCustomerEntity(CustomerDTO customer);

	List<CustomerDTO> toListCustomerDTO(List<Customer> cus);
	
	CustomerRecommendationDTO toCustomerRecommendationDTO(Customer customer);
	
	

}
