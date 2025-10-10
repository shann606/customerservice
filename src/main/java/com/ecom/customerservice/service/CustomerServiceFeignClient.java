package com.ecom.customerservice.service;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ecom.customerservice.dto.CustomerRecommendationDTO;

@FeignClient(name = "productservice")
public interface CustomerServiceFeignClient {
	

	
	@GetMapping("api/v1/products/recommendation/{productItemId}")
	List<CustomerRecommendationDTO.ProductsDTO> getRecommendedProducts(@PathVariable  UUID productItemId);

}
