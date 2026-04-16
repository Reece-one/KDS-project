package com.restaurant.KDS.service;

import com.restaurant.KDS.entity.MenuItem;
import com.restaurant.KDS.entity.Order;
import com.restaurant.KDS.entity.OrderItem;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class OrderItemServiceTest {

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private OrderService orderService;

    private MenuItem createTestMenuItem(String name) {
        MenuItem item = new MenuItem();
        item.setName(name);
        item.setPrice(BigDecimal.valueOf(10.00));
        item.setCategory("Main");
        item.setAvailable(true);
        item.setPrepTimeMinutes(3);
        item.setIngredients(new HashSet<>(List.of("bread")));
        item.setAllergens(new HashSet<>(List.of("gluten")));
        item.setStations(new ArrayList<>());
        menuService.saveMenuItem(item);
        return item;
    }

    private Order createTestOrder() {
        Order order = new Order();
        order.setTableOrName("1");
        order.setStatus("open");
        order.setEatInOrTakeAway("eat_in");
        order.setTotal(BigDecimal.valueOf(00.00));
        orderService.saveOrder(order);
        return order;
    }

    private OrderItem createTestOrderItem() {
        Order order = createTestOrder();
        MenuItem burger = createTestMenuItem("burger");
        OrderItem orderItem = new OrderItem();

        orderItem.setOrder(order);
        orderItem.setMenuItem(burger);
        orderItem.setQuantity(1);
        orderItem.setStatus("incomplete");
        orderItemService.saveOrderItem(orderItem);
        order.setTotal(orderItemService.getTotalByOrder(order));
        return orderItem;
    }

    @Test
    public void testCreateOrderItem() {
        OrderItem orderItem = createTestOrderItem();
        assertNotNull(orderItem.getId());
    }

    @Test
    public void testUpdateOrderItem() {
        OrderItem orderItem = createTestOrderItem();
        orderItem.setQuantity(2);
        orderItemService.saveOrderItem(orderItem);

        OrderItem updated = orderItemService.findById(orderItem.getId()).get();
        assertEquals(2, updated.getQuantity());
    }

    @Test
    public void testDeleteOrderItem() {
        OrderItem orderItem = createTestOrderItem();
        orderItemService.deleteOrderItem(orderItem);
        assertFalse(orderItemService.findById(orderItem.getId()).isPresent());
    }

    @Test
    public void testIncrementDuplicate() {
        OrderItem orderItem = createTestOrderItem();
        OrderItem orderItem2 = new OrderItem();
        orderItem2.setOrder(orderItem.getOrder());
        orderItem2.setMenuItem(orderItem.getMenuItem());
        orderItem2.setQuantity(3);
        orderItemService.saveOrderItem(orderItem2);

        OrderItem updated = orderItemService.findById(orderItem.getId()).get();
        assertEquals(4, updated.getQuantity());
        assertNull(orderItem2.getId());
    }

    @Test
    public void testDeleteByOrder() {
        OrderItem orderItem = createTestOrderItem();
        orderItemService.deleteByOrder(orderItem.getOrder());

        assertFalse(orderItemService.findById(orderItem.getId()).isPresent());
    }

    @Test
    public void testGetTotalByOrder() {
        OrderItem orderItem = createTestOrderItem();
        orderItemService.saveOrderItem(orderItem);

        assertEquals(0, BigDecimal.valueOf(10.00).compareTo(orderItem.getOrder().getTotal()));
    }
}