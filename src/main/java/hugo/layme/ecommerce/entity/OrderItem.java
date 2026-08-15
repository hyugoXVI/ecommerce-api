package hugo.layme.ecommerce.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "TB_ORDER_ITEMS")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    public OrderItem() {
    }

    public OrderItem(Product product, Order order,  BigDecimal unitPrice, int quantity) {

        if (quantity <= 0){
            throw new RuntimeException("Quantity must be 1 or higher!");
        }

        if (product == null){
            throw new RuntimeException("Product cannot be null!");
        }

        if (order == null){
            throw new RuntimeException("Order cannot be null!");
        }

        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0){
            throw new RuntimeException("Unit price must be positive!");
        }

        this.product = product;
        this.order = order;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = unitPrice.multiply(new BigDecimal(quantity));
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public Order getOrder() {
        return order;
    }
}
