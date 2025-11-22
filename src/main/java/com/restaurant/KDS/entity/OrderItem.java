package com.restaurant.KDS.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @OneToOne
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column(name = "modifications", columnDefinition = "TEXT")
    private String modifications;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "status", nullable = false)
    private String status = "Incomplete";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public OrderItem() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public Order getOrder() {return order;}
    public void setOrder(Order order) {this.order = order;}

    public String modifications() {return modifications;}
    public void setModifications(String modifications) {this.modifications = modifications;}

    public MenuItem getMenuItem() {return menuItem;}
    public void setMenuItem(MenuItem menuItem) {this.menuItem = menuItem;}

    public Integer getQuantity() {return quantity;}
    public void setQuantity(Integer quantity) {this.quantity = quantity;}

    public String getStatus() {return status;}
    public void setStatus(String status) {this.status = status;}

    public LocalDateTime getCreatedAt() {return createdAt;}
}
