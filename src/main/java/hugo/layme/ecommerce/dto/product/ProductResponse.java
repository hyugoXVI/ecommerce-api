package hugo.layme.ecommerce.dto.product;

import java.math.BigDecimal;

public record ProductResponse(Long id,
                              String name,
                              String description,
                              BigDecimal price,
                              Integer stock,
                              boolean active) {
}
