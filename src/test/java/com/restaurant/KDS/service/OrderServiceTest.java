package com.restaurant.KDS.service;

import com.restaurant.KDS.entity.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Test
    public void createTestOrder() {
        Order order = new Order();
        order.setTableOrName("1");
        order.setStatus("open");
        order.setEatInOrTakeAway("eat_in");
        order.setTotal(BigDecimal.valueOf(10.00));
        orderService.saveOrder(order);

        assertNotNull(order.getId());
    }

    @Test
    public void testFindByStatus() {
        Order order = new Order();
        order.setTableOrName("1");
        order.setStatus("open");
        order.setEatInOrTakeAway("eat_in");
        order.setTotal(BigDecimal.valueOf(10.00));
        orderService.saveOrder(order);

        List<Order> openOrders = orderService.findByStatus("Open");
        assertFalse(openOrders.isEmpty());
        assertTrue(openOrders.stream().allMatch(o -> o.getStatus().equals("Open")));
    }

    @Test
    public void testUpdateOrderStatus() {
        Order order = new Order();
        order.setTableOrName("1");
        order.setStatus("open");
        order.setEatInOrTakeAway("eat_in");
        order.setTotal(BigDecimal.valueOf(10.00));
        orderService.saveOrder(order);

        order.setStatus("complete");
        orderService.saveOrder(order);

        Order updatedOrder = orderService.findById(order.getId()).get();
        assertEquals("complete", updatedOrder.getStatus());
    }



}
