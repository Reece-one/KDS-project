package com.restaurant.KDS.service;

import com.restaurant.KDS.entity.Station;
import com.restaurant.KDS.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StationService {

    private final StationRepository stationRepository;

    @Autowired
    public StationService(StationRepository stationRepository) {

        this.stationRepository = stationRepository;
    }

    public void saveStation(Station station) {

        stationRepository.save(station);
    }

    public void deleteStation(Station station) {

        stationRepository.delete(station);
    }

    public Optional<Station> findById (Long id) {

        return stationRepository.findById(id);
    }

    public List<Station> getAllStations() {

        return stationRepository.findAll();
    }
}