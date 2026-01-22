package com.ecom.customerservice.validate;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.ecom.customerservice.dto.AddressDTO;
import com.ecom.customerservice.dto.CustomersDTO.CustomerDTO;

@Component
public class CustomerValidator implements Validator {

	private static final Pattern specialCharPattern = Pattern.compile("[[<>^&%#*()]]", Pattern.CASE_INSENSITIVE);

	@Override
	public boolean supports(Class<?> clazz) {
		return CustomerDTO.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		CustomerDTO customer = (CustomerDTO) target;
		if (customer.getName().isBlank()) {
			errors.reject("Null Name", "Name filed cannot be null or empty");
			}else if(checkSplChars(customer.getName())) {
			errors.reject("Invalid name","Name filed contains special characters");
		}else if(checkZipCode(customer.getAddress())) {
			errors.reject("Invalid zipcode","Zipcode cannot be null or empty");
		}

	}

	private boolean checkZipCode(List<AddressDTO> address) {

		return address.stream().filter(z -> z.getZipcode().isBlank()).count() > 0;
	}

	private boolean checkSplChars(String s) {

		Matcher m = specialCharPattern.matcher(s);
		return m.find();

	}
	
	
	
}
