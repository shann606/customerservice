package com.ecom.customerservice.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.ecom.customerservice.repository")
@EnableCaching
public class JPAConfig {

}
