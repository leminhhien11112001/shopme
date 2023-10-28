package com.shopme.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.common.entity.Customer;
import com.shopme.security.CustomerUserDetails;

@Controller
public class CustomerController {
	
	@Autowired 
	private CustomerService service;

	@GetMapping("/register")
	public String showRegisterForm(Model model) {

		model.addAttribute("pageTitle", "Customer Registration");
		model.addAttribute("customer", new Customer());

		return "register/register_form";
	}
	
	@PostMapping("/create_customer")
	public String createCustomer(Customer customer, RedirectAttributes redirectAttributes){
		service.registerCustomer(customer);
				
		redirectAttributes.addFlashAttribute("message", "Registration Succeeded!");
		
		return "redirect:/login";
	}
	
	@GetMapping("/account_details")
	public String viewAccountDetails(@AuthenticationPrincipal CustomerUserDetails loggedCustomer, Model model) {
		String email = loggedCustomer.getUsername();
		
		Customer customer = service.getCustomerByEmail(email);

		model.addAttribute("customer", customer);

		return "customer/account_form";
	}
	
	@PostMapping("/update_account_details")
	public String updateAccountDetails(@AuthenticationPrincipal CustomerUserDetails loggedCustomer,
			Model model, Customer customer, RedirectAttributes ra) {
		service.update(customer);
		
		loggedCustomer.setFirstName(customer.getFirstName());
		loggedCustomer.setLastName(customer.getLastName());

		ra.addFlashAttribute("message", "Your account details have been updated.");
						
		return "redirect:/account_details";
	}

}