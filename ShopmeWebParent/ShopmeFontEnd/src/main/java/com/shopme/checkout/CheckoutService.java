package com.shopme.checkout;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Product;

@Service
public class CheckoutService {

	public CheckoutInfo prepareCheckout(List<CartItem> cartItems) {
		CheckoutInfo checkoutInfo = new CheckoutInfo();

		float productCost = calculateProductCost(cartItems);
		float productTotal = calculateProductTotal(cartItems);
		float shippingCostTotal = calculateShippingCost(cartItems);
		float paymentTotal = productTotal + shippingCostTotal;

		checkoutInfo.setProductCost(productCost);
		checkoutInfo.setProductTotal(productTotal);
		checkoutInfo.setShippingCostTotal(shippingCostTotal);
		checkoutInfo.setPaymentTotal(paymentTotal);
		checkoutInfo.setDeliverDays(3); //Default

		return checkoutInfo;
	}

	private float calculateShippingCost(List<CartItem> cartItems) {
		float shippingCostTotal = 0.0f;

		for (CartItem item : cartItems) {
			Product product = item.getProduct();
			float shippingCost = product.getShippingCost();

			item.setShippingCost(shippingCost);

			shippingCostTotal += shippingCost;
		}

		return shippingCostTotal;
	}

	private float calculateProductTotal(List<CartItem> cartItems) {
		float total = 0.0f;

		for (CartItem item : cartItems) {
			total += item.getSubtotal();
		}

		return total;
	}

	private float calculateProductCost(List<CartItem> cartItems) {
		float cost = 0.0f;

		for (CartItem item : cartItems) {
			cost += item.getQuantity() * item.getProduct().getCost();
		}

		return cost;
	}
}