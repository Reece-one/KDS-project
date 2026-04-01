package com.restaurant.KDS.service;

import com.restaurant.KDS.entity.Order;
import com.restaurant.KDS.entity.OrderItem;
import com.restaurant.KDS.repository.OrderItemRepository;
import jakarta.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    public OrderItem saveOrderItem(OrderItem orderItem) {
        //For editing an existing Order item, updates the item
        if (orderItem.getId() != null) {
            return orderItemRepository.save(orderItem);
        }

        //Checks if the new order item is a duplicate, if so then increment the quantity
        Optional<OrderItem> existing = orderItemRepository.findByOrderAndMenuItemAndModifications(
                orderItem.getOrder(), orderItem.getMenuItem(), orderItem.getModifications()
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

    @Transactional
    public void deleteByOrder(Order order) {
        orderItemRepository.deleteByOrder(order);
    }

    public BigDecimal getTotalByOrder(Order order) {
        return orderItemRepository.getTotalByOrder(order);
    }
}
