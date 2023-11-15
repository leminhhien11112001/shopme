package com.shopme.checkout;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerService;
import com.shopme.order.OrderService;
import com.shopme.shoppingcart.ShoppingCartService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CheckoutController {

	@Autowired 
	private CheckoutService checkoutService;
	
	@Autowired 
	private CustomerService customerService;
	
	@Autowired 
	private ShoppingCartService cartService;
	
	@Autowired 
	private OrderService orderService;

	@GetMapping("/checkout")
	public String showCheckoutPage(Model model, HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);

		String defaultAddress = customer.getAddress();
		
		model.addAttribute("shippingAddress", defaultAddress);

		List<CartItem> cartItems = cartService.listCartItems(customer);
		CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems);

		model.addAttribute("checkoutInfo", checkoutInfo);
		model.addAttribute("cartItems", cartItems);
		model.addAttribute("customer", customer);
		
		return "checkout/checkout";
	}

	private Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String email = cartService.getEmailOfAuthenticatedCustomer(request);				
		return customerService.getCustomerByEmail(email);
	}	
	
	@PostMapping("/place_order")
	public String placeOrder(HttpServletRequest request) {
		String paymentMethod = request.getParameter("paymentMethod");
		String status = "NEW";
		
		
		Customer customer = getAuthenticatedCustomer(request);
		
		List<CartItem> cartItems = cartService.listCartItems(customer);
		CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems);

		orderService.createOrder(customer, cartItems, checkoutInfo, paymentMethod, status);
		
		cartService.deleteByCustomer(customer);

		return "checkout/order_completed";
	}
}