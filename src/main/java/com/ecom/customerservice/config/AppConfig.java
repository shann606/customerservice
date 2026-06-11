package com.ecom.customerservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import feign.Capability;
import feign.micrometer.MicrometerCapability;
import io.micrometer.core.instrument.MeterRegistry;

@Configuration

public class AppConfig {

	@Bean
	RestTemplate getRestTemplate() {

		return new RestTemplate();
	}

	@Bean
	Capability capability(MeterRegistry meterRegistry) {
		return new MicrometerCapability(meterRegistry);
	}

}
