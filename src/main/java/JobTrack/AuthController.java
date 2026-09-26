package JobTrack;

import java.security.NoSuchAlgorithmException;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import JobTrack.Exceptions.EmailAlreadyExistsException;
import JobTrack.Exceptions.UsernameAlreadyExistsException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired 
    private RefreshTokenService refreshTokenService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request)throws NoSuchAlgorithmException{
        if(userRepository.findByUsername(request.getUsername()).isPresent()){
            throw new UsernameAlreadyExistsException("Username already exists");
        }
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setEmail(request.getEmail());
        newUser.setSpecialty(request.getSpecialty());
        newUser.setYearsOfExperience(request.getYearsOfExperience());
        newUser.setRole(Role.ROLE_USER);

        userRepository.save(newUser);

        String token = jwtService.generateToken(newUser.getUsername());
        String refreshToken = refreshTokenService.createRefreshToken(newUser);
        return ResponseEntity.ok(new AuthResponse(token,refreshToken));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request)throws NoSuchAlgorithmException{

        System.out.println(">>> LOGIN CONTROLLER");
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        System.out.println(">>> AUTHENTICATION SUCCESSFUL");

        String token = jwtService.generateToken(request.getUsername());
        Optional<User> user = userRepository.findByUsername(request.getUsername());
        String refreshToken = refreshTokenService.createRefreshToken(user.get());
        return ResponseEntity.ok(new AuthResponse(token,refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshRequest request)throws NoSuchAlgorithmException{
        VerifyRefreshResponse response = refreshTokenService.verifyRefreshToken(request.getToken());
        String access = jwtService.generateToken(response.getUser().getUsername());

        return ResponseEntity.ok(new AuthResponse(access, response.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@Valid @RequestBody LogoutRequest request)throws NoSuchAlgorithmException{
        refreshTokenService.logout(request.getToken());
        return ResponseEntity.accepted().body("logout successful !");
    }
}
