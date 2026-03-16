package com.restaurant.KDS.service;

import com.restaurant.KDS.entity.Order;
import com.restaurant.KDS.repository.OrderRepository;

public class OrderService {

    public final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }
}
