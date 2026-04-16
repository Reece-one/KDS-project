package com.restaurant.KDS.service;

import com.restaurant.KDS.entity.Order;
import com.restaurant.KDS.entity.OrderStation;
import com.restaurant.KDS.entity.Station;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class OrderStationServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private StationService stationService;

    @Autowired
    private OrderStationService orderStationService;

    public Order createTestOrder() {
        Order order = new Order();
        order.setTableOrName("1");
        order.setStatus("open");
        order.setEatInOrTakeAway("eat_in");
        order.setTotal(BigDecimal.valueOf(10.00));
        orderService.saveOrder(order);
        return order;
    }

    public Station createTestStation(String name) {
        Station station = new Station();
        station.setName(name);
        stationService.saveStation(station);
        return station;
    }

    public OrderStation createTestOrderStation(Station station, Order order) {
        OrderStation orderStation = new OrderStation();
        orderStation.setStation(station);
        orderStation.setOrder(order);
        orderStation.setCompleted(false);
        orderStationService.save(orderStation);

        return orderStation;
    }

    @Test
    public void testCreateOrderStation() {
        Order order = createTestOrder();
        Station grill = createTestStation("Grill");
        OrderStation orderStation = createTestOrderStation(grill, order);

        assertNotNull(orderStation.getId());
    }

    @Test
    public void findByOrderAndStation() {
        Order order = createTestOrder();
        Station grill = createTestStation("Grill");
        OrderStation orderStation = createTestOrderStation(grill, order);

        Optional<OrderStation> found = orderStationService.findByOrderAndStation(order, grill);
        assertTrue(found.isPresent());
        assertEquals(order.getId(), found.get().getOrder().getId());
        assertEquals(grill.getId(), found.get().getStation().getId());
    }

    @Test
    public void testMarkComplete() {
        Order order = createTestOrder();
        Station grill = createTestStation("Grill");
        OrderStation orderStation = createTestOrderStation(grill, order);

        orderStation.setCompleted(true);

        assertTrue(orderStation.isCompleted());
    }

    @Test
    public void testAllStationsComplete() {
        Order order = createTestOrder();
        Station grill = createTestStation("Grill");
        OrderStation grillStation = createTestOrderStation(grill, order);

        Station fry =  createTestStation("Fry");
        OrderStation fryStation = createTestOrderStation(fry, order);

        grillStation.setCompleted(true);
        fryStation.setCompleted(true);
        orderStationService.save(fryStation);
        orderStationService.save(grillStation);

        assertTrue(orderStationService.allStationsCompleted(order));
    }

    @Test
    public void testNotAllStationsComplete() {
        Order order = createTestOrder();
        Station grill = createTestStation("Grill");
        OrderStation grillStation = createTestOrderStation(grill, order);

        Station fry =  createTestStation("Fry");
        OrderStation fryStation = createTestOrderStation(fry, order);

        fryStation.setCompleted(true);
        orderStationService.save(fryStation);
        orderStationService.save(grillStation);

        assertFalse(orderStationService.allStationsCompleted(order));
    }

}
