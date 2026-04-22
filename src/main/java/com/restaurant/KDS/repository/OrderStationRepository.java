package com.restaurant.KDS.repository;

import com.restaurant.KDS.entity.Order;
import com.restaurant.KDS.entity.OrderStation;
import com.restaurant.KDS.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderStationRepository extends JpaRepository<OrderStation, Long> {

    Optional<OrderStation> findByOrderAndStation(Order order, Station station);

    List<OrderStation> findByOrder(Order order);

    List<OrderStation> findByStationAndCompleted(Station station, boolean completed);

    /**
     * Gets all orders for a station where the order is "Open" and the orderstation
     * is not completed.
     *
     * @param station the current station
     * @return the orders as a list
     */
    @Query("""
      select os.order from OrderStation os
      where os.station = :station
        and os.completed = false
        and os.order.status = 'Open'
  """)
    List<Order> findOpenOrdersForStation(@Param("station") Station station);

    void deleteByStation(Station station);
}
