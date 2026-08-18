package hugo.layme.ecommerce.dto;

import org.springframework.http.HttpStatus;

import java.time.Instant;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
