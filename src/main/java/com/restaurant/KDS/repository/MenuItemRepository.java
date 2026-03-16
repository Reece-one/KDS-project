package com.restaurant.KDS.repository;

import com.restaurant.KDS.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {


}
