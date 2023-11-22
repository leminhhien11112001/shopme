package com.shopme.admin.order;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.admin.agency.AgencyRepository;
import com.shopme.common.entity.Agency;
import com.shopme.common.entity.Order;
import com.shopme.common.entity.OrderTrack;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class OrderService {
	public static final int ORDERS_PER_PAGE = 4;

	@Autowired 
	private OrderRepository repo;
	
	@Autowired
	private AgencyRepository agencyRepo;

	public Page<Order> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);
		
		if(keyword != null) {
			return repo.findAll(keyword, pageable);
		}
		
		return repo.findAll(pageable);	
	}
	
	public void delete(Integer id) throws OrderNotFoundException {
		Long count = repo.countById(id);
		if (count == null || count == 0) {
			throw new OrderNotFoundException("Could not find any orders with ID " + id); 
		}

		repo.deleteById(id);
	}	
	
	public List<Agency> listAgencies() {
		List<Agency> agencies = agencyRepo.findAll();
		
		return agencies;
	}
	
	public Order get(Integer id) throws OrderNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new OrderNotFoundException("Could not find any orders with ID " + id);
		}
	}
	
	public void save(Order orderInForm) {
		if (repo.existsById(orderInForm.getId())) {
			Order orderInDB = repo.findById(orderInForm.getId()).get();
			orderInForm.setOrderTime(orderInDB.getOrderTime());
			orderInForm.setCustomer(orderInDB.getCustomer());
		} else {
			orderInForm.setOrderTime(new Date());
		}
		repo.save(orderInForm);
	}	
	
	public void updateStatus(Integer orderId, String status) {
		Order orderInDB = repo.findById(orderId).get();
		String statusToUpdate = status;

		if (!orderInDB.hasStatus(statusToUpdate)) {
			List<OrderTrack> orderTracks = orderInDB.getOrderTracks();

			OrderTrack track = new OrderTrack();
			track.setOrder(orderInDB);
			track.setStatus(statusToUpdate);
			track.setUpdatedTime(new Date());
			
			if(status.equals("PICKED")) {
				track.setNotes("Shipper picked the package");
			}else if(status.equals("SHIPPING")) {
				track.setNotes("Shipper is delivering the package");
			}else if(status.equals("DELIVERED")) {
				track.setNotes("Customer received products");
			}else if(status.equals("RETURNED")){
				track.setNotes("Products were returned");
			}
			
			orderTracks.add(track);

			orderInDB.setStatus(statusToUpdate);

			repo.save(orderInDB);
		}

	}

	public Order findOrder(Integer id) {
		// TODO Auto-generated method stub
		return repo.findOrder(id);
	}

}
