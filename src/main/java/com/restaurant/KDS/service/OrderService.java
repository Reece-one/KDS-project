package com.restaurant.KDS.service;

import com.restaurant.KDS.controller.settings.SettingsController;
import com.restaurant.KDS.entity.Order;
import com.restaurant.KDS.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.prefs.Preferences;

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

    public boolean isOnTime(Order order) {
        Preferences prefs = Preferences.userNodeForPackage(SettingsController.class);
        if (order.getOpenedAt() == null) return true;
        double elapsed = java.time.Duration.between(order.getOpenedAt(), java.time.LocalDateTime.now()).toMillis() / 60000.0;
        return elapsed <= prefs.getInt("lateOrderTime", 7);
    }

}
