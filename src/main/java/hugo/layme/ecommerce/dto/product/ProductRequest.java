package hugo.layme.ecommerce.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProductRequest(@NotBlank String name,
                             String description,
                             @Positive @NotNull BigDecimal price,
                             @PositiveOrZero @NotNull Integer stock) {
}
