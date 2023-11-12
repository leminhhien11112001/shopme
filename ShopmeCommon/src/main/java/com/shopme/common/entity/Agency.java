package com.shopme.common.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "agencies")
public class Agency {
	@Id
	private Integer id;
	
	@Column(length = 64, nullable = false, unique = true)
	private String name;

	@Column(length = 128, nullable = false)
	private String address;
	
	private boolean enabled;
	
	@OneToMany(mappedBy = "agency", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<User> users = new HashSet<>();
	
	@OneToMany(mappedBy = "agency", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Category> categoies = new ArrayList<>();
	
	@OneToMany(mappedBy = "agency", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Order> orders = new ArrayList<>();
	
	public Agency() {
		
	}

	public Agency(String name, String address, Set<User> users, List<Category> categoies) {
		this.name = name;
		this.address = address;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public List<Category> getCategoies() {
		return categoies;
	}

	public void setCategoies(List<Category> categoies) {
		this.categoies = categoies;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	@Transient
	public String getAgency() {
		return this.id + " - " + this.name;
	}

	@Override
	public String toString() {
		return this.id + " - " + this.name;
	}
}
