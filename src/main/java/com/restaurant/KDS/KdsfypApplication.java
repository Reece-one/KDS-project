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
    }
}
