package hugo.layme.ecommerce.dto.order;

import hugo.layme.ecommerce.entity.Order;
import hugo.layme.ecommerce.entity.Product;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record OrderItemRequest(@NotNull Long productId,
                               @NotNull @Positive int quantity) {
}
