package com.shopme.common.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "orders")
public class Order {
	@Id
	private Integer id;

	private Date orderTime;

	private float shippingCost;
	
	private float productCost;
	
	private float total; //productPrice + shippingCost
	
	private String destination;

	private int deliverDays;
	
	private Date deliverDate;

	private String paymentMethod;

	private String status;

	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<OrderDetail> orderDetails = new HashSet<>();
	
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("updatedTime ASC")
	private List<OrderTrack> orderTracks = new ArrayList<>();
	
	@ManyToOne
	@JoinColumn(name = "agency_id")
	private Agency agency;
	
	public Order() {
		
	}

	public Order(Integer id, Date orderTime, float productCost, float total) {
		this.id = id;
		this.orderTime = orderTime;
		this.productCost = productCost;
		this.total = total;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}

	public float getShippingCost() {
		return shippingCost;
	}

	public void setShippingCost(float shippingCost) {
		this.shippingCost = shippingCost;
	}

	public float getProductCost() {
		return productCost;
	}

	public void setProductCost(float productCost) {
		this.productCost = productCost;
	}

	public float getTotal() {
		return total;
	}

	public void setTotal(float total) {
		this.total = total;
	}

	public int getDeliverDays() {
		return deliverDays;
	}

	public void setDeliverDays(int deliverDays) {
		this.deliverDays = deliverDays;
	}

	public Date getDeliverDate() {
		return deliverDate;
	}

	public void setDeliverDate(Date deliverDate) {
		this.deliverDate = deliverDate;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Set<OrderDetail> getOrderDetails() {
		return orderDetails;
	}

	public void setOrderDetails(Set<OrderDetail> orderDetails) {
		this.orderDetails = orderDetails;
	}
	
	public List<OrderTrack> getOrderTracks() {
		return orderTracks;
	}

	public void setOrderTracks(List<OrderTrack> orderTracks) {
		this.orderTracks = orderTracks;
	}
	
	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getDestination() {
		return destination;
	}
	
	public Agency getAgency() {
		return agency;
	}

	public void setAgency(Agency agency) {
		this.agency = agency;
	}

	@Transient
	public String getDeliverDateOnForm() {
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormatter.format(this.deliverDate);
	}	
	
	@Override
	public String toString() {
		return "Order [id=" + id + ", total=" + total + ", paymentMethod=" + paymentMethod + ", status=" + status
				+ ", customer=" + customer.getFullName() + "]";
	}
	
	public void setDeliverDateOnForm(String dateString) {
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

		try {
			this.deliverDate = dateFormatter.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		} 		
	}
	
	@Transient
	public boolean isCOD() {
		return paymentMethod.equals("COD");
	}

	@Transient
	public boolean isPicked() {
		return hasStatus("PICKED");
	}

	@Transient
	public boolean isShipping() {
		return hasStatus("SHIPPING");
	}

	@Transient
	public boolean isDelivered() {
		return hasStatus("DELIVERED");
	}

	@Transient
	public boolean isReturned() {
		return hasStatus("RETURNED");
	}	

	public boolean hasStatus(String status) {
		for (OrderTrack aTrack : orderTracks) {
			if (aTrack.getStatus().equals(status)) {
				return true;
			}
		}

		return false;
	}
}
