package hugo.layme.ecommerce.dto.order;

import hugo.layme.ecommerce.entity.Order;
import hugo.layme.ecommerce.entity.Product;

import java.math.BigDecimal;

public record OrderItemResponse(Long id,
                                String productName,
                                BigDecimal unitPrice,
                                int quantity,
                                BigDecimal subtotal) {
}
