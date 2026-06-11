package com.ecom.customerservice.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.ecom.customerservice.dto.CustomerRecommendationDTO;
import com.ecom.customerservice.dto.CustomerRecommendationDTO.ProductsDTO;
import com.ecom.customerservice.dto.GenderEnum;
import com.ecom.customerservice.entity.Customer;
import com.ecom.customerservice.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

@SpringBootTest
@WireMockTest
class CustomerServiceIntegrationTest {

	@MockitoBean
	private CustomerRepository customerRepo;
	@MockitoBean
	private CustomerServiceFeignClient feignClient;

	@Autowired
	private CustomerService customerService;

	private static WireMockServer wireMockServer = new WireMockServer(Options.DYNAMIC_PORT);

	private static CustomerRecommendationDTO recommandation;

	@Autowired
	private ObjectMapper objectMapper;

	private static Optional<Customer> customers;

	static List<ProductsDTO> items;

	@BeforeAll
	static void testInit() {
		wireMockServer.start();

		items = new ArrayList<CustomerRecommendationDTO.ProductsDTO>();
		customers = Optional.ofNullable(Customer.builder().id(UUID.fromString("23df78e8-1eb0-476c-b8b5-a2e31ea79ab6"))
				.age(50).firtName("Shanmugam").lastName("Swaminathan").name("Shanmugam").build());

		UUID customerID = UUID.fromString("23df78e8-1eb0-476c-b8b5-a2e31ea79ab6");
		UUID productItemId = UUID.fromString("99a23a22-4617-48bb-bd5e-fa4c09f421b5");

		recommandation = new CustomerRecommendationDTO(customerID, "Shanmugam", 50, "Shanmugam", "Swaminathan",
				GenderEnum.MALE, null, null);

		ProductsDTO products = new ProductsDTO(customerID, productItemId, "Loues Votion",
				"This is one of the Amazing brand", 0, new BigDecimal(12300), true, OffsetDateTime.now(),
				OffsetDateTime.now(), "Admin", "Admin");

		items.add(products);

	}

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {

		registry.add("productservice", () -> "http://localhost:" + wireMockServer.port());
	}

	@BeforeEach

	void testStart() {

		when(customerRepo.findById(UUID.fromString("23df78e8-1eb0-476c-b8b5-a2e31ea79ab6"))).thenReturn(customers);
		when(feignClient.getRecommendedProducts(UUID.fromString("99a23a22-4617-48bb-bd5e-fa4c09f421b5"))).thenReturn(items);

	}

	@Test
	void testFindCustomerProduct() throws Exception {

		wireMockServer
				.stubFor(get(urlMatching("/api/v1/categories/recommendation/99a23a22-4617-48bb-bd5e-fa4c09f421b5"))
						.willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
								.withBody(objectMapper.writeValueAsString(recommandation))));
	CustomerRecommendationDTO result = customerService.findCustomerProduct(
				UUID.fromString("23df78e8-1eb0-476c-b8b5-a2e31ea79ab6"),
				UUID.fromString("99a23a22-4617-48bb-bd5e-fa4c09f421b5"));

		assertNotNull(result);
		assertEquals(result.getId(), UUID.fromString("23df78e8-1eb0-476c-b8b5-a2e31ea79ab6"));

		wireMockServer.verify(0,
				getRequestedFor(urlEqualTo("/api/v1/categories/recommendation/99a23a22-4617-48bb-bd5e-fa4c09f421b5")));

	}

}
