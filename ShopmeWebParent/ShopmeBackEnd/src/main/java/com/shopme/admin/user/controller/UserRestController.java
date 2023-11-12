package com.shopme.admin.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.admin.user.UserService;

@RestController
public class UserRestController {
	
	@Autowired
	private UserService service;
	
	@PostMapping("/users/check_id")
	public String checkValidId(Integer oldId, Integer id, Integer agencyId, String email) {
		return service.checkValid(oldId, id, agencyId, email);
	}
}
