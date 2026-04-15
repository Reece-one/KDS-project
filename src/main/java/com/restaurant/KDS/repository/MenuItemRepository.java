package com.restaurant.KDS.repository;

import com.restaurant.KDS.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.restaurant.KDS.entity.Station;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByStationsContaining(Station station);

    @Query("SELECT DISTINCT m.category FROM MenuItem m")
    List<String> findDistinctCategories();

    List<MenuItem> findByCategory(String category);
}
