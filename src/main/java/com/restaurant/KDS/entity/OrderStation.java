package com.restaurant.KDS.entity;

import jakarta.persistence.*;

/**
 * An entity that represents the links between a {@link Station} and {@link Order}.
 */
@Entity
@Table(indexes = {
        @Index(name = "idx_orderstation_station_completed", columnList = "station_id, completed"),
        @Index(name = "idx_orderstation_order_station", columnList = "order_id, station_id")
})
public class OrderStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Order order;

    @ManyToOne
    private Station station;

    private boolean completed = false;

    private boolean recalled = false;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Station getStation() { return station; }
    public void setStation(Station station) { this.station = station; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public boolean isRecalled() { return recalled; }
    public void setRecalled(boolean recalled) { this.recalled = recalled; }
}
