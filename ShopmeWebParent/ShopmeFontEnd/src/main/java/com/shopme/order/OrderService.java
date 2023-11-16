package com.shopme.order;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.checkout.CheckoutInfo;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Order;
import com.shopme.common.entity.OrderDetail;
import com.shopme.common.entity.OrderTrack;
import com.shopme.common.entity.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class OrderService {
	
	public static final int ORDERS_PER_PAGE = 5;

	@Autowired 
	private OrderRepository repo;
	
	@Autowired
	private OrderTrackRepository orderTrackRepository;

	public Order createOrder(Customer customer, List<CartItem> cartItems, CheckoutInfo checkoutInfo,
			String paymentMethod, String status) {
		Order newOrder = new Order();
		
		newOrder.setOrderTime(new Date());
		newOrder.setStatus(status);
		newOrder.setCustomer(customer);
		newOrder.setProductCost(checkoutInfo.getProductCost());
		newOrder.setShippingCost(checkoutInfo.getShippingCostTotal());
		newOrder.setTotal(checkoutInfo.getPaymentTotal());
		newOrder.setPaymentMethod(paymentMethod);
		newOrder.setDeliverDays(checkoutInfo.getDeliverDays());
		newOrder.setDeliverDate(checkoutInfo.getDeliverDate());
		newOrder.setDestination(customer.getAddress());
		
		OrderTrack orderTrack = new OrderTrack();
		
		orderTrack.setStatus(status);
		orderTrack.setNotes("Order was placed by customer");
		orderTrack.setUpdatedTime(new Date());
		orderTrack.setOrder(newOrder);
		
		List<OrderTrack> orderTracks = new ArrayList<>();
		orderTracks.add(orderTrack);
		
		newOrder.setOrderTracks(orderTracks);
		
		Set<OrderDetail> orderDetails = newOrder.getOrderDetails();

		for (CartItem cartItem : cartItems) {
			Product product = cartItem.getProduct();

			OrderDetail orderDetail = new OrderDetail();
			orderDetail.setOrder(newOrder);
			orderDetail.setProduct(product);
			orderDetail.setQuantity(cartItem.getQuantity());
			orderDetail.setUnitPrice(product.getDiscountPrice());
			orderDetail.setProductCost(product.getCost() * cartItem.getQuantity());
			orderDetail.setSubtotal(cartItem.getSubtotal());
			orderDetail.setShippingCost(cartItem.getShippingCost());

			orderDetails.add(orderDetail);
		}
		
		repo.save(newOrder);
		orderTrackRepository.save(orderTrack);

		return newOrder;
		
	}
	
	public Page<Order> listForCustomerByPage(Customer customer, int pageNum, 
			String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

		Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);

		if (keyword != null) {
			return repo.findAll(keyword, customer.getId(), pageable);
		}

		return repo.findAll(customer.getId(), pageable);

	}
}