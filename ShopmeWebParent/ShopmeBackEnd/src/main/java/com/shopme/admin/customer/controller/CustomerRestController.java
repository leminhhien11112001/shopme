package com.shopme.admin.customer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.admin.customer.CustomerService;
import com.shopme.common.entity.Customer;
import com.shopme.common.exception.CustomerNotFoundException;

@RestController
public class CustomerRestController {
	@Autowired
	private CustomerService service;

	@PostMapping("/customers/check_email")
	public String checkDuplicateEmail(Integer oldId, Integer id, String email) {
		return service.isEmailUnique(oldId, id, email); 
	}
	
	@GetMapping("orders/customer/{id}")
	public Customer getInfo(@PathVariable(name = "id") Integer cusId) throws CustomerNotFoundException{
		return service.get(cusId);
	}
}