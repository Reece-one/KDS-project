package com.restaurant.KDS.service;

import com.restaurant.KDS.entity.MenuItem;
import com.restaurant.KDS.entity.Station;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
public class MenuServiceTest {
    @Autowired
    private MenuService menuService;

    @Autowired
    private StationService stationService;

    @Autowired
    private EntityManager entityManager;

    private MenuItem createTestMenuItem(String name) {
        MenuItem item = new MenuItem();
        item.setName(name);
        item.setPrice(BigDecimal.valueOf(10.00));
        item.setCategory("Main");
        item.setAvailable(true);
        item.setPrepTimeMinutes(3);
        item.setIngredients(new ArrayList<>(List.of("bread", "cheese")));
        item.setAllergens(new ArrayList<>(List.of("gluten")));
        item.setStations(new ArrayList<>());
        menuService.saveMenuItem(item);
        return item;
    }

    @Test
    public void testAddMenuItem() {
        //Create stations first
        Station grill = new Station();
        grill.setName("grill");
        stationService.saveStation(grill);

        Station set = new Station();
        set.setName("set");
        stationService.saveStation(set);

        MenuItem burger = new MenuItem();
        burger.setName("Burger");
        burger.setPrice(BigDecimal.valueOf(10.00));
        burger.setCategory("Main");

        List<String> ingredients = new ArrayList<>();
        ingredients.add("bread");
        ingredients.add("beef patty");
        ingredients.add("cheese");
        ingredients.add("pickle");
        ingredients.add("onion");
        ingredients.add("tomato");
        ingredients.add("sauce");
        burger.setIngredients(ingredients);

        List<String> allergens = new ArrayList<>();
        allergens.add("gluten");
        burger.setAllergens(allergens);

        burger.setAvailable(true);
        burger.setPrepTimeMinutes(3);

        List<Station> stations = new ArrayList<>();
        stations.add(grill);
        stations.add(set);
        burger.setStations(stations);

        menuService.saveMenuItem(burger);

        assertNotNull(burger.getId());
        assertEquals("Burger", burger.getName());
        assertEquals(7, burger.getIngredients().size());
        assertEquals(1, burger.getAllergens().size());
        assertEquals(2, burger.getStations().size());
    }

    @Test
    public void testDeleteMenuItem() {
        MenuItem burger = createTestMenuItem("Burger");
        menuService.deleteMenuItem(burger);
        assertFalse(menuService.findById(burger.getId()).isPresent());
    }

    @Test
    public void testUpdateMenuItem() {
        MenuItem burger = createTestMenuItem("Burger");
        burger.setName("Big Burger");
        burger.setPrice(BigDecimal.valueOf(15.00));
        burger.setPrepTimeMinutes(4);
        menuService.saveMenuItem(burger);

        entityManager.flush();
        entityManager.clear();

        MenuItem updated = menuService.findById(burger.getId()).get();
        assertEquals("Big Burger", updated.getName());
    }

    @Test
    public void testDuplicateMenuItemName() {
        createTestMenuItem("Burger");
        assertThrows(Exception.class, () -> {
            createTestMenuItem("Burger");
        });
    }
}
