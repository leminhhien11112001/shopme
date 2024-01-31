package com.shopme.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.common.entity.product.Product;

@RestController
public class ProductRestController {

	@Autowired
	private ProductRepository repo;

	@GetMapping("/get_quantity")
	public String getQuantityOfProduct(Integer productId) {
		Product product = repo.findById(productId).get();
		return String.valueOf(product.getQuantity());
	}
}
