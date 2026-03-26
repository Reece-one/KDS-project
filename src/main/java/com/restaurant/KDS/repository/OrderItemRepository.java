package com.restaurant.KDS.repository;

import com.restaurant.KDS.entity.MenuItem;
import com.restaurant.KDS.entity.Order;
import com.restaurant.KDS.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    Optional<OrderItem> findByOrderAndMenuItem(Order order, MenuItem menuItem);
}
