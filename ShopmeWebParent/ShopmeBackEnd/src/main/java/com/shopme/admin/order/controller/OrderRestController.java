package com.shopme.admin.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.admin.customer.CustomerService;
import com.shopme.admin.order.OrderService;
import com.shopme.common.entity.Customer;
import com.shopme.common.exception.CustomerNotFoundException;

@RestController
public class OrderRestController {
	@Autowired
	private OrderService service;

	@PostMapping("/orders/check_id")
	public String checkDuplicateEmail(Integer oldId, Integer id) {
		return service.isIdUnique(oldId, id); 
	}
	
}