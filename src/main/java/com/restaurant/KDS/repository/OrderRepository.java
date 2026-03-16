package com.restaurant.KDS.repository;

import com.restaurant.KDS.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
