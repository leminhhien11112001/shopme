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

@Service
public class OrderService {

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
}