package com.ecom.customerservice.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "online_address")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address {

	@Id
	@Column(name = "id", nullable = false)
	private UUID id;
	@Column(name = "customer_id", nullable = false, insertable = false, updatable = false)
	private UUID customerId;
	private String addr1;
	private String addr2;
	private String city;
	private String state;
	private String zipcode;
	private String country;

	@PrePersist
	private void generateId() {
		if (id == null) {
			id = UUID.randomUUID();
		}

	}

}
