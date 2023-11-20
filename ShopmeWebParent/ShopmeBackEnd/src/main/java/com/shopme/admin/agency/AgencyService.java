package com.shopme.admin.agency;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Agency;
import com.shopme.common.entity.Customer;
import com.shopme.common.exception.AgencyNotFoundException;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AgencyService {
	public static final int AGENCIES_PER_PAGE = 5;
	
	@Autowired
	private AgencyRepository repo;
	
	public Page<Agency> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

		Pageable pageable = PageRequest.of(pageNum - 1, AGENCIES_PER_PAGE, sort);

		if (keyword != null) {
			return repo.findAll(keyword, pageable);
		}

		return repo.findAll(pageable);
	}

	public void updateAgencyEnabledStatus(Integer id, boolean enabled) {
		repo.updateEnabledStatus(id, enabled);
	}

	public Agency get(Integer id) throws AgencyNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new AgencyNotFoundException("Could not find any agencies with ID " + id);
		}
	}
	
	public void save(Agency agency) {
		repo.save(agency);
	}

	public boolean isNameUnique(Integer id, String name) {
		Agency existAgency = repo.findByName(name);

		if (existAgency != null && !existAgency.getId().equals(id)) {
			return false;
		}

		return true;
	}

	public boolean isIdUnique(Integer id, Integer oldId) {
		boolean isNew = (oldId == null) ;
		
		Agency agencyById = repo.findAgencyById(id);
		
		if (isNew && agencyById != null) return false;
		
		return true;
	}
	
	public void delete(Integer id) throws AgencyNotFoundException {
		Long countById = repo.countById(id);

		if (countById == null || countById == 0) {
			throw new AgencyNotFoundException("Could not find any brand with ID " + id);			
		}
		
		repo.deleteById(id);
	}
}
