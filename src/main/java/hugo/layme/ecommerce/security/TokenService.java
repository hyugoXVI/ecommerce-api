package hugo.layme.ecommerce.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import hugo.layme.ecommerce.dto.AuthResponse;
import hugo.layme.ecommerce.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenService {

    private final String secret;

    public TokenService(@Value("${spring.security.secret}") String secret) {
        this.secret = secret;
    }

    public AuthResponse generateToken(User user){

        Instant now = Instant.now();

        return new AuthResponse(
            JWT.create()
                    .withIssuer("ecommerce-api-hugo-layme")
                    .withIssuedAt(now)
                    .withExpiresAt(now.plus(2, ChronoUnit.DAYS))
                    .withSubject(user.getEmail())
                    .sign(Algorithm.HMAC256(secret))
        );
    }

    public String validateToken(String token){

        try{

        return JWT.
                require(Algorithm.HMAC256(secret))
                .withIssuer("ecommerce-api-hugo-layme")
                .build()
                .verify(token)
                .getSubject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
