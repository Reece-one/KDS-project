package com.restaurant.KDS.service;

import com.restaurant.KDS.entity.OrderItem;
import com.restaurant.KDS.repository.OrderItemRepository;

public class OrderItemService {

    public final OrderItemRepository orderItemRepository;

    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    public OrderItem save(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }



}
