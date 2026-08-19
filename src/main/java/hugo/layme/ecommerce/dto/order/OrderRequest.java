package hugo.layme.ecommerce.dto.order;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderRequest(@NotEmpty List<OrderItemRequest> items) {
}
