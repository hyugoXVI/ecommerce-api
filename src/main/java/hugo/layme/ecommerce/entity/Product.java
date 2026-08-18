package hugo.layme.ecommerce.entity;

import hugo.layme.ecommerce.exception.BusinessRuleException;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "TB_PRODUCTS")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    private boolean active;

    @Version
    private Long version;

    public Product() {
    }

    public Product(String name, String description, BigDecimal price, Integer stock) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getStock() {
        return stock;
    }

    public boolean isActive() {
        return active;
    }

    public void activate(){
        this.active = true;
    }

    public void deactivate(){
        this.active = false;
    }

    public void increaseStock(int quantity){
        if (quantity <= 0){
            throw new BusinessRuleException("Quantity must be positive.");
        }
        this.stock += quantity;
    }

    public void decreaseStock(int quantity){

        if (quantity <= 0){
            throw new BusinessRuleException("Quantity must be positive.");
        }
        if (quantity > stock){
            throw new BusinessRuleException("Quantity must be lower than product's stock");
        }

        this.stock -= quantity;
    }
}
