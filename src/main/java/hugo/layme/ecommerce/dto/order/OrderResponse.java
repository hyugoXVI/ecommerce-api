package hugo.layme.ecommerce.dto.order;

import hugo.layme.ecommerce.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse (Long id,
                            Long userId,
                            String userName,
                            BigDecimal total,
                            OrderStatus status,
                            Instant createdAt,
                             List<OrderItemResponse> items){
}
