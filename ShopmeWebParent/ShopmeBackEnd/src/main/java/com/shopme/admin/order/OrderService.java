package com.shopme.admin.order;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.common.entity.Order;
import com.shopme.common.exception.OrderNotFoundException;


@Service
public class OrderService {
	private static final int ORDERS_PER_PAGE = 10;

	@Autowired private OrderRepository repo;

	public void listByPage(int pageNum, PagingAndSortingHelper helper) {		
		helper.listEntities(pageNum, ORDERS_PER_PAGE, repo);
	}
	
	public Order get(Integer id) throws OrderNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new OrderNotFoundException("Could not find any orders with ID " + id);
		}
	}
}