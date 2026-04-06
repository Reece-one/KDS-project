package com.restaurant.KDS.repository;

import com.restaurant.KDS.entity.Order;
import com.restaurant.KDS.entity.OrderStation;
import com.restaurant.KDS.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderStationRepository extends JpaRepository<OrderStation, Long> {

    Optional<OrderStation> findByOrderAndStation(Order order, Station station);

    List<OrderStation> findByOrder(Order order);

    List<OrderStation> findByStationAndCompleted(Station station, boolean completed);
}
