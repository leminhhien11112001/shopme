package com.shopme.admin.category;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.Category;

@Repository 
public interface CategoryRepository extends	PagingAndSortingRepository<Category, Integer>
				, CrudRepository<Category, Integer>{

	@Query("SELECT c FROM Category c WHERE c.parent.id is NULL")
	public List<Category> findRootCategories(Sort sort);
	
	public Category findByName(String name);

	public Category findByAlias(String alias);
}