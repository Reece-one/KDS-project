package com.restaurant.KDS.repository;

import com.restaurant.KDS.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {


}
