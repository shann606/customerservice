package com.ecom.customerservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ecom.customerservice.dto.CustomerRecommendationDTO;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@FeignClient(name = "productservice")
public interface CustomerServiceFeignClient {

	@GetMapping("api/v1/products/recommendation/{productItemId}")
	@CircuitBreaker(name = "productservice", fallbackMethod = "fallBackProductStr")
	String getRecommendedProductsAsString(@PathVariable UUID productItemId);

	@GetMapping("api/v1/products/recommendation/{productItemId}")
	@CircuitBreaker(name = "productservice", fallbackMethod = "fallBackProduct")
	List<CustomerRecommendationDTO.ProductsDTO> getRecommendedProducts(@PathVariable UUID productItemId);

	default String fallBackProductStr(UUID productItemId, Throwable t) {

		return "Product service is down please try later";
	}

	default List<CustomerRecommendationDTO.ProductsDTO> fallBackProduct(UUID productItemId, Throwable t) {
		return new ArrayList<>();

	}

}
