package com.ecom.customerservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;

@OpenAPIDefinition
@Configuration
public class OpenAPIConfig {
	
	private static final String CONTENT_TYPE="application/json";
	private static final String DEFAULT="default";

	@Bean
	OpenAPI getOpenApi() {

		ApiResponse badRequest = new ApiResponse().content(new io.swagger.v3.oas.models.media.Content().addMediaType(
				CONTENT_TYPE, new io.swagger.v3.oas.models.media.MediaType().addExamples(DEFAULT, new Example()
						.value("{\"code\" : 400, \"status\" : \"Bad Request\", \"Message\" : \"Bad Request\"}"))));

		ApiResponse internalServerError = new ApiResponse()
				.content(new io.swagger.v3.oas.models.media.Content().addMediaType(CONTENT_TYPE,
						new io.swagger.v3.oas.models.media.MediaType().addExamples(DEFAULT, new Example().value(
								"{\"code\" : 500, \"status\" : \"InternalServerError\", \"Message\" : \"Bad Request\"}"))));

		ApiResponse successfulResponse = new ApiResponse()
				.content(new io.swagger.v3.oas.models.media.Content().addMediaType(CONTENT_TYPE,
						new io.swagger.v3.oas.models.media.MediaType().addExamples(DEFAULT, new Example().value(
								"{\"code\" : 200, \"status\" : \"Success\", \"Message\" : \"Request is successfull\"}"))));

		Components components = new Components();
		components.addResponses("badRequest", badRequest);
		components.addResponses("internalServerError", internalServerError);
		components.addResponses("successfulResponse", successfulResponse);

		return new OpenAPI().info(new Info().title("Customer Service").version("v1")
				.description("this is customer service of our online shopping").summary(
						"this is customer service is has intergrated with all other services of our online shopping applicaiton"))
				.components(components);

	}

}
