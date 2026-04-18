package com.restaurant.KDS.service;

import com.restaurant.KDS.entity.Order;
import com.restaurant.KDS.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;


    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> findByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    /**
     * Checks whether the {@link Order} is late or not.
     *
     * @param order                the order that the comparison is done on
     * @param lateOrderTimeMinutes the time in minutes after which the order is considered late
     * @return {@code true} if the elapsed time is less than or equal to {@code lateOrderTimeMinutes},
     * or if the order has not been opened yet
     */
    public boolean isOnTime(Order order, int lateOrderTimeMinutes) {
        if (order.getOpenedAt() == null) return true;
        double elapsed = Duration.between(order.getOpenedAt(), LocalDateTime.now()).toMillis() / 60000.0;
        return elapsed <= lateOrderTimeMinutes;
    }

}
