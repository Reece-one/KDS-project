package com.restaurant.KDS.repository;

import com.restaurant.KDS.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
