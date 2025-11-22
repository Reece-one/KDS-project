package com.restaurant.KDS;

import com.restaurant.KDS.entity.MenuItem;
import com.restaurant.KDS.service.MenuService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class KdsfypApplication implements CommandLineRunner {

    private final MenuService menuService;

    public KdsfypApplication(MenuService menuService) {
        this.menuService = menuService;
    }

    public static void main(String[] args) { SpringApplication.run(KdsfypApplication.class, args);}

    @Override
    public void run(String... args) throws Exception {
        MenuItem burger = new MenuItem();
        burger.setName("Burger");
        burger.setPrice(BigDecimal.valueOf(10.00));
        burger.setCategory("Main");

        List<String> ingredients = new ArrayList<>();
        ingredients.add("Bread");
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

        List<String> stations = new ArrayList<>();
        stations.add("grill");
        stations.add("set");

        burger.setStations(stations);

        menuService.saveMenuItem(burger);

        List<MenuItem> menuItems = menuService.getAllMenuItems();
        System.out.println("Menu items in database: " + menuItems.size());
        menuItems.forEach(item -> System.out.println("- " + item.getName()));

    }
}
