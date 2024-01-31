package com.shopme.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.ProductNotFoundException;

@RestController
public class ProductRestController {

	@Autowired
	private ProductService service;

	@GetMapping("products/get_quantity")
	public String getQuantityOfProduct(Integer productId) throws ProductNotFoundException {
		Product product = service.getProduct(productId);
		return String.valueOf(product.getQuantity());
	}
}
