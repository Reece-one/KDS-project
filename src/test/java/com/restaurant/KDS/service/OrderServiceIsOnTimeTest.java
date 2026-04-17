package com.restaurant.KDS.service;

import com.restaurant.KDS.entity.Order;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderServiceIsOnTimeTest {

    private final OrderService service = new OrderService(null);

    @Test
    void nullOpenedAt_returnsTrue() {
        Order order = new Order();
        order.setOpenedAt(null);

        assertTrue(service.isOnTime(order, 7));
    }

    @Test
    void justOpened_returnsTrue() {
        Order order = new Order();
        order.setOpenedAt(LocalDateTime.now());

        assertTrue(service.isOnTime(order, 7));
    }

    @Test
    void openedLongerThanThreshold_returnsFalse() {
        Order order = new Order();
        order.setOpenedAt(LocalDateTime.now().minusMinutes(30));

        assertFalse(service.isOnTime(order, 7));
    }

    @Test
    void openedExactlyAtThreshold_returnsTrue() {
        Order order = new Order();
        order.setOpenedAt(LocalDateTime.now().minusMinutes(7));

        assertTrue(service.isOnTime(order, 7));
    }
}
