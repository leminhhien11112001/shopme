package com.shopme.admin.product;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer>,
											CrudRepository<Product, Integer>{
	public Product findByName(String name);
	
}