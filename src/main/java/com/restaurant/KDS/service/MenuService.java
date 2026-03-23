package com.restaurant.KDS.service;

import com.restaurant.KDS.entity.MenuItem;
import com.restaurant.KDS.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MenuService {

    private final MenuItemRepository menuItemRepository;

    @Autowired
    public MenuService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    public void saveMenuItem(MenuItem item) {
        menuItemRepository.save(item);
    }

    public void deleteMenuItem(MenuItem item) {
        menuItemRepository.delete(item);
    }

    public Optional<MenuItem> findById(Long id) {
       return menuItemRepository.findById(id);
    }
}
