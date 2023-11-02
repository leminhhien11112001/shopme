package com.shopme.admin.agency.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.admin.agency.AgencyService;

@RestController
public class AgencyRestController {
	@Autowired
	private AgencyService service;
	
	@PostMapping("/agencies/check_name")
	public String checkDuplicateName(Integer id, String name) {
		if (service.isNameUnique(id, name)) {
			return "OK";
		} else {
			return "Duplicated";
		}
	}

}
