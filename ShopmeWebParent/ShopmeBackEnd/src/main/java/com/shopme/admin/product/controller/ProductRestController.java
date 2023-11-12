package com.shopme.admin.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.admin.product.ProductService;

@RestController
public class ProductRestController {

	@Autowired 
	private ProductService service;

	@PostMapping("/products/check_unique")
	public String checkUnique(Integer oldId, Integer cateId, Integer id, String name) {
		return service.checkUnique(oldId, cateId, id, name);
	}	
}