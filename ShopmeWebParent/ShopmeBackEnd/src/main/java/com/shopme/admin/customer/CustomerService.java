package com.shopme.admin.customer;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;
import com.shopme.common.exception.CustomerNotFoundException;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CustomerService {
	
	public static final int CUSTOMERS_PER_PAGE = 10;

	@Autowired 
	private CustomerRepository customerRepo;
	

	public List<Customer> listAll() {
		return (List<Customer>) customerRepo.findAll();
	}

	public Page<Customer> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

		Pageable pageable = PageRequest.of(pageNum - 1, CUSTOMERS_PER_PAGE, sort);

		if (keyword != null) {
			return customerRepo.findAll(keyword, pageable);
		}

		return customerRepo.findAll(pageable);
	}

	public void updateCustomerEnabledStatus(Integer id, boolean enabled) {
		customerRepo.updateEnabledStatus(id, enabled);
	}

	public Customer get(Integer id) throws CustomerNotFoundException {
		try {
			return customerRepo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new CustomerNotFoundException("Could not find any customers with ID " + id);
		}
	}
	

	public String isEmailUnique(Integer oldId, Integer id, String email) {
		Customer existCustomerById = customerRepo.getCustomerById(id);
		Customer existCustomer = customerRepo.findByEmail(email);

		if (oldId == null && existCustomerById != null) return "DuplicatedId";
		
		if (existCustomer != null && existCustomer.getId() != id) {
			// found another customer having the same email
			return "Duplicated";
		}

		return "OK";
	}

	public void save(Customer customerInForm) {
		Customer customerInDB = customerRepo.getCustomerById(customerInForm.getId());
		boolean isUpdatingCustomer = (customerInDB != null);
		
		if(isUpdatingCustomer) {	
			customerInForm.setCreatedTime(customerInDB.getCreatedTime());
		}else {
			customerInForm.setCreatedTime(new Date());
		}
		
		
		customerRepo.save(customerInForm);
	}

	public void delete(Integer id) throws CustomerNotFoundException {
		Long count = customerRepo.countById(id);
		if (count == null || count == 0) {
			throw new CustomerNotFoundException("Could not find any customers with ID " + id);
		}

		customerRepo.deleteById(id);
	}

}
