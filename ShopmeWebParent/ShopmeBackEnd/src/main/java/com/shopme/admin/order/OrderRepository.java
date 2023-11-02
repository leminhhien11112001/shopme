package com.shopme.admin.order;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Order;

public interface OrderRepository extends CrudRepository<Order, Integer>,
				PagingAndSortingRepository<Order, Integer> {
	
	@Query("SELECT o FROM Order o WHERE CONCAT('#', o.id) LIKE %?1% OR o.customer.firstName LIKE %?1% OR"
			+ " o.customer.lastName LIKE %?1% OR o.customer.phoneNumber LIKE %?1% OR"
			+ " o.paymentMethod LIKE %?1% OR o.status LIKE %?1% OR o.destination LIKE %?1%")
	public Page<Order> findAll(String keyword, Pageable pageable);
	
	public Long countById(Integer id);
	
	@Query("SELECT NEW com.shopme.common.entity.Order(o.id, o.orderTime, o.productCost,"
			+ " o.total) FROM Order o WHERE"
			+ " DATE(o.orderTime) BETWEEN DATE(?1) AND DATE(?2) ORDER BY o.orderTime ASC")
	public List<Order> findByOrderTimeBetween(Date startTime, Date endTime);
}
