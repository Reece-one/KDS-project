package com.restaurant.KDS.service;

import com.restaurant.KDS.entity.Station;
import com.restaurant.KDS.repository.StationRepository;

public class StationService {

    public final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Station saveStation(Station station) {
        return stationRepository.save(station);
    }
}