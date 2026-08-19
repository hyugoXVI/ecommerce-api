package hugo.layme.ecommerce.service;

import hugo.layme.ecommerce.dto.authentication.AuthResponse;
import hugo.layme.ecommerce.dto.authentication.LoginRequest;
import hugo.layme.ecommerce.dto.authentication.RegisterRequest;
import hugo.layme.ecommerce.entity.User;
import hugo.layme.ecommerce.exception.BusinessRuleException;
import hugo.layme.ecommerce.repository.UserRepository;
import hugo.layme.ecommerce.security.TokenService;
import hugo.layme.ecommerce.security.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public AuthService(PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager
            , UserRepository userRepository, TokenService tokenService) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    public void register(RegisterRequest request){

        userRepository.findByEmail(request.email())
                .ifPresentOrElse(u -> {
                    throw new BusinessRuleException("Provided e-mail has already taken");
                }, () -> {
                    userRepository.save(new User(
                            request.name(), request.email(), passwordEncoder.encode(request.password())
                    ));
                });
    }

    public AuthResponse login(LoginRequest request) {

        UsernamePasswordAuthenticationToken login = new UsernamePasswordAuthenticationToken(request.email(), request.password());

        Authentication authentication = authenticationManager.authenticate(login);


        if (!(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)){
            throw new IllegalStateException("Unexpected authenticated principal.");
        }

        return tokenService.generateToken(userDetails.getUser());
    }
}
