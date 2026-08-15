package hugo.layme.ecommerce.dto.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest (@Email String email,
                            @NotBlank String password){
}
