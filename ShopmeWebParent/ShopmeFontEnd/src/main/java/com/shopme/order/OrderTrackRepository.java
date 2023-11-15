package com.shopme.order;

import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.OrderTrack;

public interface OrderTrackRepository extends CrudRepository<OrderTrack, Integer>{

}
