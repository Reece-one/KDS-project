package com.restaurant.KDS.service;

import com.restaurant.KDS.entity.Station;
import com.restaurant.KDS.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class StationServiceTest {

    @Autowired
    private StationService stationService;

    @Test
    public void testCreateStation() {
        Station grill = new Station();
        grill.setName("Grill");
        stationService.saveStation(grill);
        assertNotNull(grill.getId());
        assertEquals("Grill", grill.getName());
    }

    @Test
    public void testDeleteStationByID() {
        Station station = new Station();
        station.setName("Station");
        stationService.saveStation(station);

        stationService.deleteStation(station);
        assertFalse(stationService.findById(station.getId()).isPresent());
    }

    @Test
    public void testEditStationName() {
        Station station = new Station();
        station.setName("Station");
        stationService.saveStation(station);

        station.setName("Grill");
        stationService.saveStation(station);

        Station updated = stationService.findById(station.getId()).get();
        assertEquals("Grill", updated.getName());
    }

    @Test
    public void testCreateWithBlankName() {
        Station station = new Station();
        station.setName("");
        assertThrows(Exception.class, () -> stationService.saveStation(station));
    }
}
