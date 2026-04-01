package com.restaurant.KDS.repository;

import com.restaurant.KDS.entity.MenuItem;
import com.restaurant.KDS.entity.Order;
import com.restaurant.KDS.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    Optional<OrderItem> findByOrderAndMenuItemAndModifications(Order order, MenuItem menuItem, String modifications);

    void deleteByOrder(Order order);

    @Query("SELECT SUM(oi.menuItem.price * oi.quantity) FROM OrderItem oi WHERE oi.order = :order")
    BigDecimal getTotalByOrder(@Param("order") Order order);
}
