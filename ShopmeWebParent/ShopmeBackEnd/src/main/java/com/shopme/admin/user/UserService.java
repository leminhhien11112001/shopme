package com.shopme.admin.user;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.admin.agency.AgencyRepository;
import com.shopme.common.entity.Agency;
import com.shopme.common.entity.User;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {
	
	public static final int USERS_PER_PAGE = 4;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private AgencyRepository agencyRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public User getByEmail(String email) {
		return userRepo.getUserByEmail(email);
	}
	
	public List<User> listAll() {
		return (List<User>) userRepo.findAll();
	}

	public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);
		
		if(keyword != null) {
			return userRepo.findAll(keyword, pageable);
		}
		
		return userRepo.findAll(pageable);
	}
	
	public List<String> listRoles() {
		List<String> roles = new ArrayList<>();
		
		roles.add("Admin");
		roles.add("Salesperson");
		roles.add("Editor");
		roles.add("Shipper");
		roles.add("Assistant");
		
		return roles;
	}
	
	public List<Agency> listAgencies() {
		List<Agency> agencies = agencyRepo.findAll();
		
		return agencies;
	}
	
	public User save(User user) {
		boolean isUpdatingUser = userRepo.existsById(user.getId());

		if (isUpdatingUser) {
			User existingUser = userRepo.findById(user.getId()).get();

			if (user.getPassword().isEmpty()) {
				user.setPassword(existingUser.getPassword());
			} else {
				encodePassword(user);
			}

		} else {		
			encodePassword(user);
		}
		return userRepo.save(user);
	}
	
	public User updateAccount(User userInForm) {
		User userInDB = userRepo.findById(userInForm.getId()).get();

		if (!userInForm.getPassword().isEmpty()) {
			userInDB.setPassword(userInForm.getPassword());
			encodePassword(userInDB);
		}

		if (userInForm.getPhotos() != null) {
			userInDB.setPhotos(userInForm.getPhotos());
		}

		userInDB.setFirstName(userInForm.getFirstName());
		userInDB.setLastName(userInForm.getLastName());

		return userRepo.save(userInDB);
	}
	
	private void encodePassword(User user) {
		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
	}
	
	public boolean isEmailUnique(Integer oldId, Integer id, String email) {
		User userByEmail = userRepo.getUserByEmail(email);

		if(userByEmail == null) return true;
		
		boolean isCreatingNew = (oldId == null);
		
		if(isCreatingNew) {
			if(userByEmail != null) return false;
		}else {
			if(!userByEmail.getId().equals(id)) {
				return false;
			}
		}
		
		return true;
	}

	public User get(Integer id) throws UserNotFoundException {
		try {
			return userRepo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new UserNotFoundException("Could not find any user with ID " + id);
		}
	}

	public void delete(Integer id) throws UserNotFoundException{
		Long countById = userRepo.countById(id);
		if (countById == null || countById == 0) {
			throw new UserNotFoundException("Could not find any user with ID " + id);
		}

		userRepo.deleteById(id);
	}

	public void updateUserEnabledStatus(Integer id, boolean enabled) {
		userRepo.updateEnabledStatus(id, enabled);
	}

	public boolean isIdValid(Integer oldId, Integer id, Integer agencyId) {
		
		if ((id / 100) != agencyId) return false;
		User userById = userRepo.getUserById(id);

		if(userById == null) return true;
		
		boolean isCreatingNew = (oldId == null);
		
		if(isCreatingNew) {
			if(userById != null) return false;
		}
		
		return true;
	}

	
}
