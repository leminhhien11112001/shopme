package com.shopme.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Product;

public interface ProductRepository extends
	CrudRepository<Product, Integer>, PagingAndSortingRepository<Product, Integer> {

	@Query("SELECT p FROM Product p WHERE p.enabled = true "
			+ "AND (p.category.id = ?1)"
			+ " ORDER BY p.name ASC")
	public Page<Product> listByCategory(Integer categoryId, Pageable pageable);
	
	public Product findByAlias(String alias);
	
	@Query("SELECT p FROM Product p WHERE p.id = ?1")
	public Product getById(Integer id);
	
	@Query(value = "SELECT * FROM products WHERE enabled = true AND "
			+ "MATCH(name, short_description, full_description) AGAINST (?1)", 
			nativeQuery = true)
	public Page<Product> search(String keyword, Pageable pageable);
	

//	@Query("Update Product p SET p.averageRating = COALESCE((SELECT AVG(r.rating) FROM Review r WHERE r.product.id = ?1), 0),"
//			+ " p.reviewCount = (SELECT COUNT(r.id) FROM Review r WHERE r.product.id =?1) "
//			+ "WHERE p.id = ?1")
//	@Modifying
//	public void updateReviewCountAndAverageRating(Integer productId);
	
	@Query("UPDATE Product p SET p.averageRating = COALESCE(CAST((SELECT AVG(CAST(r.rating AS DOUBLE)) FROM Review r WHERE r.product.id = ?1) AS FLOAT), 0.0),"
	        + " p.reviewCount = (SELECT COUNT(r.id) FROM Review r WHERE r.product.id = ?1) "
	        + "WHERE p.id = ?1")
	@Modifying
	public void updateReviewCountAndAverageRating(Integer productId);
	
	@Query("SELECT p FROM Product p ORDER BY p.averageRating DESC LIMIT ?1")
	public List<Product> findByAverageRating (Integer n);

}