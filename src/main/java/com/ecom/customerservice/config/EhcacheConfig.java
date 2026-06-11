package com.ecom.customerservice.config;

import java.util.concurrent.TimeUnit;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.spi.CachingProvider;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ecom.customerservice.dto.CustomersDTO;

@Configuration
public class EhcacheConfig {

	@Bean
	CacheManager getCacheManager() {
		CachingProvider provider = Caching.getCachingProvider();
		javax.cache.CacheManager cacheManager = provider.getCacheManager();

		MutableConfiguration<Long, CustomersDTO> config = new MutableConfiguration<Long, CustomersDTO>()
				.setStoreByValue(false).setExpiryPolicyFactory(
						CreatedExpiryPolicy.factoryOf(new javax.cache.expiry.Duration(TimeUnit.MINUTES, 10)));

		if (cacheManager.getCache("customers") != null) {
			cacheManager.createCache("customers", config);
		}
		return cacheManager;

	}

}
