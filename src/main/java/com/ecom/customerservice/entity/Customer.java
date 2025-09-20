package com.ecom.customerservice.entity;

import java.util.List;
import java.util.UUID;

import com.ecom.customerservice.util.GenderEnum;

import jakarta.annotation.Generated;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Table(name = "online_customers")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer {
	
	@Id
	@Column(name = "id" , nullable = false)
	private UUID id;
	private String name;
	private int age;
	private String firtName;
	private String lastName;
	private GenderEnum gender;
	
	@OneToMany(orphanRemoval = true,cascade = CascadeType.ALL)
	@JoinColumn(name = "customer_id", nullable = false)
	List<Address> address;
	
	@PrePersist
	private void generateId() {
		if (id == null) {
			id = UUID.randomUUID();
		}

	}

	

}
