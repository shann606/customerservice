package com.ecom.customerservice.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ValidationDTO implements Serializable {

	private static final long serialVersionUID = 1648253161382349526L;

	private String status;
	private String errorFiled;
	private String reason;

}
