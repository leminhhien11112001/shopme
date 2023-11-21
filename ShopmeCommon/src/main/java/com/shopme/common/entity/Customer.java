package com.shopme.common.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "customers")
public class Customer {

	@Id
	private Integer id;

	@Column(nullable = false, unique = true, length = 45)
	private String email;

	@Column(name = "first_name", nullable = false, length = 45)
	private String firstName;

	@Column(name = "last_name", nullable = false, length = 45)
	private String lastName;

	@Column(name = "phone_number", nullable = false, length = 15)
	private String phoneNumber;

	@Column(nullable = false, length = 64)
	private String address;

	private boolean enabled;

	@Column(name = "created_time")
	private Date createdTime;

	public Customer() {
	}

	public Customer(Integer id) {
		this.id = id;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	@Transient
	public String getFullName() {
		return firstName + " " + lastName;
	}

	@Override
	public String toString() {
		return "Customer [id=" + id + ", email=" + email + ", firstName=" + firstName + ", lastName=" + lastName + "]";
	}


}