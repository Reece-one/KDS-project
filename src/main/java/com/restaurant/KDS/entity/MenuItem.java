package com.restaurant.KDS.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An entity that represents an item on the menu.
 */
@Entity
@Table(name = "menu_items")
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private String category;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "menu_item_ingredients",
            joinColumns = @JoinColumn(name = "menu_item_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"menu_item_id", "ingredient"})
    )
    @Column(name = "ingredient")
    private Set<String> ingredients = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "menu_item_allergens", joinColumns = @JoinColumn(name = "menu_item_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"menu_item_id", "allergen"})
    )
    @Column(name = "allergen")
    private Set<String> allergens = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "menu_item_stations", joinColumns = @JoinColumn(name = "menu_item_id"), inverseJoinColumns = @JoinColumn(name = "station_id"))
    private List<Station> stations = new ArrayList<>();

    @Column(nullable = false)
    private Boolean available = true;

    @Column(name = "prep_time_minutes", nullable = false)
    private Integer prepTimeMinutes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public MenuItem() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public  String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public BigDecimal getPrice() {return price;}
    public void setPrice(BigDecimal price) {this.price = price;}

    public String getCategory() {return category;}
    public void setCategory(String category) {this.category = category;}

    public Set<String> getIngredients() {return ingredients;}
    public void setIngredients(Set<String> ingredients) {this.ingredients = ingredients;}

    public List<Station> getStations() {return stations;}
    public void setStations(List<Station> stations) {this.stations = stations;}

    public Set<String> getAllergens() {return allergens;}
    public void setAllergens(Set<String> allergens) {this.allergens = allergens;}

    public Boolean getAvailable() {return available;}
    public void setAvailable(Boolean available) {this.available = available;}

    public Integer getPrepTimeMinutes() {return prepTimeMinutes;}
    public void setPrepTimeMinutes(Integer time) {this.prepTimeMinutes = time;}

    public LocalDateTime getCreatedAt() {return createdAt;}

    public LocalDateTime getUpdatedAt() {return updatedAt;}

}
