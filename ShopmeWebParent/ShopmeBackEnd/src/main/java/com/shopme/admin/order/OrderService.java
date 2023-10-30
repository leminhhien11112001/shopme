package com.shopme.admin.order;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Order;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class OrderService {
	public static final int ORDERS_PER_PAGE = 4;

	@Autowired 
	private OrderRepository repo;

	public Page<Order> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);
		
		if(keyword != null) {
			return repo.findAll(keyword, pageable);
		}
		
		return repo.findAll(pageable);	
	}
	
	public void delete(Integer id) throws OrderNotFoundException {
		Long count = repo.countById(id);
		if (count == null || count == 0) {
			throw new OrderNotFoundException("Could not find any orders with ID " + id); 
		}

		repo.deleteById(id);
	}	
	
	public Order get(Integer id) throws OrderNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new OrderNotFoundException("Could not find any orders with ID " + id);
		}
	}
	
	public void save(Order orderInForm) {
		Order orderInDB = repo.findById(orderInForm.getId()).get();
		orderInForm.setOrderTime(orderInDB.getOrderTime());
		orderInForm.setCustomer(orderInDB.getCustomer());

		repo.save(orderInForm);
	}	
}
