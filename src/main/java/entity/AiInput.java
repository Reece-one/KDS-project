package entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table (name = "ai_input")
public class AiInput {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "input")
    private String input;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public AiInput() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public Order getOrder() {return order;}
    public void setOrder(Order order) {this.order = order;}

    public String getInput() {return input;}
    public void setInput(String input) {this.input = input;}

    public String getCategory() {return category;}
    public void setCategory(String category) {this.category = category;}

    public LocalDateTime getCreatedAt() {return createdAt;}
}
