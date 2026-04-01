package com.restaurant.KDS.service;

import com.restaurant.KDS.entity.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

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

}
