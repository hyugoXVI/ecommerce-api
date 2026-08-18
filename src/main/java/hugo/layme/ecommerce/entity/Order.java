package hugo.layme.ecommerce.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "TB_ORDERS")
public class Order {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    public Order() {
    }

    public Order(User user) {

        if (user == null){
            throw new RuntimeException("User cannot be null.");
        }

        this.user = user;
        this.total = BigDecimal.ZERO;
        this.status = OrderStatus.PENDING;
        this.createdAt = Instant.now();

    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public List<OrderItem> getOrderItems() {
        return Collections.unmodifiableList(orderItems);
    }

    public BigDecimal getTotal() {
        return total;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void addItem(OrderItem item){

        if (item == null){
            throw new RuntimeException("Item cannot be null!");
        }

        this.total = total.add(item.getSubtotal());
        this.orderItems.add(item);

    }

    public void pay(){

        if (this.status == OrderStatus.CANCELLED){
            throw new RuntimeException("This order is cancelled and cannot be paid.");
        }

        if (this.status == OrderStatus.PAID){
            throw new RuntimeException("This order is already paid.");
        }

        this.status = OrderStatus.PAID;
    }

    public void cancel(){
        if (this.status == OrderStatus.PAID){
            throw new RuntimeException("This order is already paid and cannot be cancelled.");
        }
        if (this.status == OrderStatus.CANCELLED){
            throw new RuntimeException("This order is already cancelled.");
        }

        this.status = OrderStatus.CANCELLED;
    }

}
