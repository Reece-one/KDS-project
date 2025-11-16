package entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_or_name", nullable = false)
    private String tableOrName;

    @Column(nullable = false)
    private String status;

    @Column(name = "eat_in_or_takeaway", nullable = false)
    private String eatInOrTakeAway;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    private String message;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "served_at")
    private LocalDateTime servedAt;

    public Order() {}

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

    public String getTableOrName() {return tableOrName;}
    public void setTableOrName(String tableOrName) {this.tableOrName = tableOrName;}

    public String getStatus() {return status;}
    public void setStatus(String status) {this.status = status;}

    public String getEatInOrTakeAway() { return eatInOrTakeAway;}
    public void setEatInOrTakeAway(String category) {this.eatInOrTakeAway = category;}

    public BigDecimal getTotal() {return total;}
    public void setTotal(BigDecimal total) {this.total = total;}

    public String getMessage() {return message;}
    public void setMessage(String message) {this.message = message;}

    public LocalDateTime getCreatedAt() {return createdAt;}

    public LocalDateTime getUpdatedAt() {return updatedAt;}

    public LocalDateTime getCompletedAt() {return completedAt;}
    public void setCompletedAt(LocalDateTime completedAt) {this.completedAt = completedAt;}

    public LocalDateTime getServedAt() {return servedAt;}
    public void setServedAt(LocalDateTime servedAt) {this.servedAt = servedAt;}
}
