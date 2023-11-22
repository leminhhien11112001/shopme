package com.shopme.admin.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.admin.product.ProductDTO;
import com.shopme.admin.product.ProductService;
import com.shopme.common.entity.Product;
import com.shopme.common.exception.ProductNotFoundException;

@RestController
public class ProductRestController {

	@Autowired 
	private ProductService service;

	@PostMapping("/products/check_unique")
	public String checkUnique(Integer oldId, Integer cateId, Integer id, String name) {
		return service.checkUnique(oldId, cateId, id, name);
	}	
	
	@GetMapping("/products/get/{id}")
	public ProductDTO getProductInfo(@PathVariable("id") Integer id) 
			throws ProductNotFoundException {
		Product product = service.get(id);
		return new ProductDTO(product.getName(), product.getMainImagePath(), 
				product.getDiscountPrice(), product.getCost());
	}
}