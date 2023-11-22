package com.shopme.admin.product;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Product;
import com.shopme.common.exception.ProductNotFoundException;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProductService {
	
	public static final int PRODUCTS_PER_PAGE = 5;

	@Autowired 
	private ProductRepository repo;

	public List<Product> listAll() {
		return (List<Product>) repo.findAll();
	}
	
	public Page<Product> listByPage(int pageNum, String sortField, String sortDir, 
			String keyword, Integer categoryId) {
		Sort sort = Sort.by(sortField);

		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE, sort);

		if (keyword != null && !keyword.isEmpty()) {
			if (categoryId != null && categoryId > 0) {
				return repo.searchInCategory(categoryId, keyword, pageable);
			}

			return repo.findAll(keyword, pageable);
		}
		
		if (categoryId != null && categoryId > 0) {
			return repo.findAllInCategory(categoryId, pageable);
		}

		return repo.findAll(pageable);		
	}
	
	public Product save(Product product) {
		Product productInDb;
		if(product.getId()<100) {
			String newId = product.getCategory().getId()+ ((product.getId() < 10)? "0":"") + product.getId(); 
			product.setId(Integer.parseInt(newId));
			productInDb = repo.getProductById(Integer.parseInt(newId));
		}else {
			productInDb = repo.getProductById(product.getId());
		}
		if (productInDb == null) {
			product.setCreatedTime(new Date());	
		}

		if (product.getAlias() == null || product.getAlias().isEmpty()) {
			String defaultAlias = product.getName().replaceAll(" ", "-");
			product.setAlias(defaultAlias);
		} else {
			product.setAlias(product.getAlias().replaceAll(" ", "-"));
		}

		product.setUpdatedTime(new Date());

		return repo.save(product);
	}
	
	public void saveProductPrice(Product productInForm) {
		Product productInDB = repo.findById(productInForm.getId()).get();
		productInDB.setCost(productInForm.getCost());
		productInDB.setPrice(productInForm.getPrice());
		productInDB.setDiscountPercent(productInForm.getDiscountPercent());

		repo.save(productInDB);
	}
	
	public String checkUnique(Integer oldId, Integer cateId, Integer id, String name) {
		boolean isCreatingNew = (oldId == null || oldId == 0);
		
		Product productByName = repo.findByName(name.trim());

		if (isCreatingNew) {
			String newId = cateId + ((id < 10)? "0":"") + id;
			Product productById = repo.getProductById(Integer.parseInt(newId));
			if (productById != null) return "DuplicateId";
			if (productByName != null) return "Duplicate";
		} else {
			if (productByName != null && !productByName.getId().equals(id)) {
				return "Duplicate";
			}
		}

		return "OK";
	}
	
	public void updateProductEnabledStatus(Integer id, boolean enabled) {
		repo.updateEnabledStatus(id, enabled);
	}	
	
	public void delete(Integer id) throws ProductNotFoundException {
		Long countById = repo.countById(id);

		if (countById == null || countById == 0) {
			throw new ProductNotFoundException("Could not find any product with ID " + id);			
		}

		repo.deleteById(id);
	}	
	
	public Product get(Integer id) throws ProductNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new ProductNotFoundException("Could not find any product with ID " + id);
		}
	}
	
	public Page<Product> searchProducts(int pageNum, String sortField, String sortDir, 
			String keyword) {
		Sort sort = Sort.by(sortField);

		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE, sort);

		if (keyword != null && !keyword.isEmpty()) {
			return repo.findAll(keyword, pageable);
		}

		return repo.findAll(pageable);		
	}
	
}