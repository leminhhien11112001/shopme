package com.shopme.admin.order;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Order;

public interface OrderRepository extends CrudRepository<Order, Integer>,
				PagingAndSortingRepository<Order, Integer> {
	
}
