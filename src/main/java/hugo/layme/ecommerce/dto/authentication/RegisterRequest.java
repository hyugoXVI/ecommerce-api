package hugo.layme.ecommerce.dto.authentication;

public record RegisterRequest(String name,
                              String email,
                              String password) {
}
