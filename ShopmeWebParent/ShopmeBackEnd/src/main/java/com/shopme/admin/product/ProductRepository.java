package com.shopme.admin.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.Product;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Integer>,
											CrudRepository<Product, Integer>{
	public Product findByName(String name);
	
	@Query("UPDATE Product p SET p.enabled = ?2 WHERE p.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id, boolean enabled);	
	
	public Long countById(Integer id);
	
	@Query("SELECT p FROM Product p WHERE p.name LIKE %?1% " 
			+ "OR p.shortDescription LIKE %?1% "
			+ "OR p.fullDescription LIKE %?1% "
			+ "OR p.brand.name LIKE %?1% "
			+ "OR p.category.name LIKE %?1%")
	public Page<Product> findAll(String keyword, Pageable pageable);
	
	@Query("SELECT p FROM Product p WHERE p.category.id = ?1")	
	public Page<Product> findAllInCategory(Integer categoryId, Pageable pageable);

	@Query("SELECT p FROM Product p WHERE p.category.id = ?1 AND"
			+ "(p.name LIKE %?2% " 
			+ "OR p.shortDescription LIKE %?2% "
			+ "OR p.fullDescription LIKE %?2% "
			+ "OR p.brand.name LIKE %?2% "
			+ "OR p.category.name LIKE %?2%)")			
	public Page<Product> searchInCategory(Integer categoryId, String keyword, Pageable pageable);
	
	@Query("Update Product p SET p.averageRating = COALESCE(CAST((SELECT AVG(r.rating) FROM Review r WHERE r.product.id = ?1) AS FLOAT), 0), "
			+ "p.reviewCount = (SELECT COUNT(r.id) FROM Review r WHERE r.product.id =?1) WHERE p.id = ?1")	
	@Modifying
	public void updateReviewCountAndAverageRating(Integer productId);
}