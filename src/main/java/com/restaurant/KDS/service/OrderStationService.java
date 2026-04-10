package com.restaurant.KDS.service;

import com.restaurant.KDS.entity.Order;
import com.restaurant.KDS.entity.OrderStation;
import com.restaurant.KDS.entity.Station;
import com.restaurant.KDS.repository.OrderStationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderStationService {

    private final OrderStationRepository orderStationRepository;

    public OrderStationService(OrderStationRepository orderStationRepository) {
        this.orderStationRepository = orderStationRepository;
    }

    public OrderStation save(OrderStation orderStation) {
        return orderStationRepository.save(orderStation);
    }

    public Optional<OrderStation> findByOrderAndStation(Order order, Station station) {
        return orderStationRepository.findByOrderAndStation(order, station);
    }

    public List<OrderStation> findByOrder(Order order) {
        return orderStationRepository.findByOrder(order);
    }

    public boolean allStationsCompleted(Order order) {
        List<OrderStation> all = orderStationRepository.findByOrder(order);
        return all.stream().allMatch(OrderStation::isCompleted);
    }

    public void markComplete(Order order, Station station) {
        Optional<OrderStation> os = orderStationRepository.findByOrderAndStation(order, station);
        os.ifPresent(record -> {
            record.setCompleted(true);
            orderStationRepository.save(record);
        });
    }

    public List<Order> findCompletedOrdersByStation(Station station) {
        return orderStationRepository.findByStationAndCompleted(station, true)
                .stream()
                .map(OrderStation::getOrder)
                .toList();
    }
}
