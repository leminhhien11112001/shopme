package com.shopme.admin.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.Category;

@Repository 
public interface CategoryRepository extends	PagingAndSortingRepository<Category, Integer>
				, CrudRepository<Category, Integer>{


	@Query("SELECT c FROM Category c WHERE CONCAT(c.id, ' ', c.name, ' ', c.alias, ' ',c.agency.name) LIKE %?1%")
	public Page<Category> findAll(String keyword, Pageable pageable);
	
	public Category findByName(String name);

	public Category findByAlias(String alias);
	
	public Long countById(Integer id);
	
	@Query("UPDATE Category c SET c.enabled = ?2 WHERE c.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id, boolean enabled);	
}
