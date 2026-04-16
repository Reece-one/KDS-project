package com.restaurant.KDS.service;

import com.restaurant.KDS.entity.MenuItem;
import com.restaurant.KDS.entity.Station;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
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
        item.setIngredients(new HashSet<>(List.of("bread", "cheese")));
        item.setAllergens(new HashSet<>(List.of("gluten")));
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

        Set<String> ingredients = new HashSet<>();
        ingredients.add("bread");
        ingredients.add("beef patty");
        ingredients.add("cheese");
        ingredients.add("pickle");
        ingredients.add("onion");
        ingredients.add("tomato");
        ingredients.add("sauce");
        burger.setIngredients(ingredients);

        Set<String> allergens = new HashSet<>();
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

    @Test
    public void testFindByCategory() {
        MenuItem burger = createTestMenuItem("Burger");
        MenuItem item2 = new MenuItem();
        item2.setName("Fries");
        item2.setPrice(BigDecimal.valueOf(5.00));
        item2.setCategory("Side");
        item2.setAvailable(true);
        item2.setPrepTimeMinutes(2);
        item2.setIngredients(new HashSet<>(List.of("potato")));
        item2.setAllergens(new HashSet<>());
        item2.setStations(new ArrayList<>());
        menuService.saveMenuItem(item2);

        List<MenuItem> mains = menuService.findByCategory("Main");
        assertFalse(mains.isEmpty());
        assertTrue(mains.stream().allMatch(m -> m.getCategory().equals("Main")));

        List<MenuItem> sides = menuService.findByCategory("Side");
        assertFalse(sides.isEmpty());
        assertTrue(sides.stream().allMatch(m -> m.getCategory().equals("Side")));
    }

    @Test
    public void testFindAllCategories() {
        createTestMenuItem("Burger");
        MenuItem item2 = new MenuItem();
        item2.setName("Fries");
        item2.setPrice(BigDecimal.valueOf(5.00));
        item2.setCategory("Side");
        item2.setAvailable(true);
        item2.setPrepTimeMinutes(2);
        item2.setIngredients(new HashSet<>(List.of("potato")));
        item2.setAllergens(new HashSet<>());
        item2.setStations(new ArrayList<>());
        menuService.saveMenuItem(item2);

        List<String> categories = menuService.getCategories();
        assertTrue(categories.contains("Main"));
        assertTrue(categories.contains("Side"));
    }
}
