package com.restaurant.KDS.service;

import com.restaurant.KDS.entity.OrderItem;
import com.restaurant.KDS.entity.Station;
import com.restaurant.KDS.repository.OrderItemRepository;
import jakarta.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    public OrderItem saveOrderItem(OrderItem orderItem) {
        //Checks if the order item already exists, if so then increment the quantity
        Optional<OrderItem> existing = orderItemRepository.findByOrderAndMenuItem(
                orderItem.getOrder(), orderItem.getMenuItem()
        );

        if (existing.isPresent()) {
            OrderItem existingItem = existing.get();
            existingItem.setQuantity(existingItem.getQuantity() + orderItem.getQuantity());
            return orderItemRepository.save(existingItem);
        }

        return orderItemRepository.save(orderItem);
    }

    public Optional<OrderItem> findById (Long id) {
        return orderItemRepository.findById(id);
    }

    public void deleteOrderItem(OrderItem orderItem) {
        orderItemRepository.delete(orderItem);
    }


}
