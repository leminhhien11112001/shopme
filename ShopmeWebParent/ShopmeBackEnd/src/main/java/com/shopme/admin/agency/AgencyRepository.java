package com.shopme.admin.agency;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Agency;

public interface AgencyRepository extends PagingAndSortingRepository<Agency, Integer>,
					CrudRepository<Agency, Integer>{
	@Query("SELECT a FROM Agency a WHERE CONCAT(a.name, ' ', a.address) LIKE %?1%")
	public Page<Agency> findAll(String keyword, Pageable pageable);

	@Query("UPDATE Agency a SET a.enabled = ?2 WHERE a.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id, boolean enabled);

	public Long countById(Integer id);
	
	@Query("SELECT a FROM Agency a WHERE a.name = ?1")
	public Agency findByName(String name);
	
	public List<Agency> findAll();

	@Query("SELECT a FROM Agency a WHERE a.id = ?1")
	public Agency findAgencyById(Integer id);
	
}
