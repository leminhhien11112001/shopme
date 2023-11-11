package com.shopme.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Category;
import com.shopme.common.exception.CategoryNotFoundException;

@Service
public class CategoryService {

	@Autowired 
	private CategoryRepository repo;

	public List<Category> listAllCategories() {
		List<Category> listEnabledCategories = repo.findAllEnabled();

		return listEnabledCategories;
	}
	
	public Category getCategory(String alias) throws CategoryNotFoundException {
		Category category = repo.findByAliasEnabled(alias);
		if (category == null) {
			throw new CategoryNotFoundException("Could not find any categories with alias " + alias);
		}

		return category;
	}
	
	//For Breadcrumb
	public List<Category> getCategoryParents(Category child) {
		List<Category> listParents = new ArrayList<>();

		listParents.add(child);

		return listParents;
	}
}