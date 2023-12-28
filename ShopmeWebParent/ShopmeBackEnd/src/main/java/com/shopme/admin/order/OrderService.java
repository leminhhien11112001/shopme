package com.shopme.admin.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.shopme.admin.paging.PagingAndSortingHelper;


@Service
public class OrderService {
	private static final int ORDERS_PER_PAGE = 10;

	@Autowired private OrderRepository repo;

	public void listByPage(int pageNum, PagingAndSortingHelper helper) {		
		helper.listEntities(pageNum, ORDERS_PER_PAGE, repo);
	}
}