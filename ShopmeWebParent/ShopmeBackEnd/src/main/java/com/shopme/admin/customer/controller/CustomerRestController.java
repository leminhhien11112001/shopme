package com.shopme.admin.customer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.admin.customer.CustomerService;

@RestController
public class CustomerRestController {
	@Autowired
	private CustomerService service;

	@PostMapping("/customers/check_email")
	public String checkDuplicateEmail(Integer oldId, Integer id, String email) {
		return service.isEmailUnique(oldId, id, email); 
	}
}
