package com.shopme.order;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class OrderDetailRepositoryTests {

	@Autowired 
	private OrderDetailRepository repo;

	@Test
	public void testCountByProductAndCustomerAndOrderStatus() {
		Integer productId = 106;
		Integer customerId = 1;

		Long count = repo.countByProductAndCustomerAndOrderStatus(productId, customerId, "DELIVERED");
		System.out.println(count);
		//		assertThat(count).isGreaterThan(0);
		
	}

}