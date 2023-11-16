package com.shopme.customer;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Customer;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class CustomerService {
	
	@Autowired 
	private CustomerRepository customerRepo;
	
	@Autowired 
	PasswordEncoder passwordEncoder;

	public boolean isEmailUnique(String email) {
		Customer customer = customerRepo.findByEmail(email);
		return customer == null;
	}
	
	public Customer getCustomerByEmail(String email) {
		return customerRepo.findByEmail(email);
	}
	
	
	public void registerCustomer(Customer customer) {
		encodePassword(customer);
		
		customer.setEnabled(true);
		customer.setCreatedTime(new Date());
		
		customerRepo.save(customer);
		
	}
	
	public void update(Customer customerInForm) {
		Customer customerInDB = customerRepo.findById(customerInForm.getId()).get();

		if (!customerInForm.getPassword().isEmpty()) {
			String encodedPassword = passwordEncoder.encode(customerInForm.getPassword());
			customerInForm.setPassword(encodedPassword);			
		} else {
			customerInForm.setPassword(customerInDB.getPassword());
		}

		customerInForm.setEnabled(customerInDB.isEnabled());
		customerInForm.setCreatedTime(customerInDB.getCreatedTime());

		customerRepo.save(customerInForm);
	}	
	
	private void encodePassword(Customer customer) {
		String encodedPassword = passwordEncoder.encode(customer.getPassword());
		customer.setPassword(encodedPassword);
	}
	
	public String getEmailOfAuthenticatedCustomer(HttpServletRequest request) {
		Object principal = request.getUserPrincipal();
		
		if (principal == null) return null;

		String customerEmail = null;

		customerEmail = request.getUserPrincipal().getName(); 
		
		return customerEmail;
	}
	
}
