package com.restaurant.KDS.repository;

import com.restaurant.KDS.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStatus(String status);

    List<Order> findAll();

    @Query("""
    select distinct o from Order o
    left join fetch o.orderItems
    where o.status in ('Open', 'Recalled')
  """)
    List<Order> findOpenAndRecalledOrders();
}
