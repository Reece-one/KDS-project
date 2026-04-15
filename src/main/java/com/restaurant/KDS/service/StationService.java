package com.restaurant.KDS.service;

import com.restaurant.KDS.entity.MenuItem;
import com.restaurant.KDS.entity.Station;
import com.restaurant.KDS.repository.MenuItemRepository;
import com.restaurant.KDS.repository.OrderStationRepository;
import com.restaurant.KDS.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StationService {

    private final StationRepository stationRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderStationRepository orderStationRepository;

    @Autowired
    public StationService(StationRepository stationRepository, MenuItemRepository menuItemRepository, OrderStationRepository orderStationRepository) {
        this.stationRepository = stationRepository;
        this.menuItemRepository = menuItemRepository;
        this.orderStationRepository = orderStationRepository;
    }

    public void saveStation(Station station) {

        stationRepository.save(station);
    }

    @Transactional
    public void deleteStation(Station station) {
        List<MenuItem> menuItems = menuItemRepository.findByStationsContaining(station);
        for (MenuItem item : menuItems) {
            item.getStations().removeIf(s -> s.getId().equals(station.getId()));
            menuItemRepository.save(item);
        }
        orderStationRepository.deleteByStation(station);
        stationRepository.delete(station);
    }

    public Optional<Station> findById (Long id) {

        return stationRepository.findById(id);
    }

    public List<Station> getAllStations() {

        return stationRepository.findAll();
    }
}