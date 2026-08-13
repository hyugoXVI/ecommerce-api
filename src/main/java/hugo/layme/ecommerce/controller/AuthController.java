package hugo.layme.ecommerce.controller;

import hugo.layme.ecommerce.dto.AuthResponse;
import hugo.layme.ecommerce.dto.LoginRequest;
import hugo.layme.ecommerce.dto.RegisterRequest;
import hugo.layme.ecommerce.entity.User;
import hugo.layme.ecommerce.repository.UserRepository;
import hugo.layme.ecommerce.security.UserDetailsImpl;
import hugo.layme.ecommerce.security.UserDetailsServiceImpl;
import hugo.layme.ecommerce.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public void register(@RequestBody RegisterRequest request) {

        authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request){

        return authService.login(request);
    }
}
