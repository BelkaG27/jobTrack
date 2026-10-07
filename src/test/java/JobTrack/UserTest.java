package JobTrack;


import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;



@ExtendWith(MockitoExtension.class)
public class UserTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks 
    CustomUserDetailService customUserDetailService;

    private PasswordEncoder encoder = new BCryptPasswordEncoder();

    private DaoAuthenticationProvider provider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(customUserDetailService);
        authProvider.setPasswordEncoder(encoder);
        return authProvider;
    }

    @ParameterizedTest
    @CsvSource({
        "true,  false, true",
        "false, false, false",
        "true,  true,  false",
        "false, true,  false"
    })
    void isEnabled_depends_on_enabled_and_suspended(boolean enabled, boolean suspended, boolean expected) {
        User user = new User("testuser", "password", "test@example.com", "Java", 5, Role.ROLE_USER);
        user.setEnabled(enabled);
        user.setSuspended(suspended);
        assertEquals(expected, user.isEnabled());
    }

    @Test
    void testAuthenticationProviderDisabledUser(){
        User user = new User("testuser", "password", "test@example.com", "Java", 5, Role.ROLE_USER);
        user.setEnabled(true);
        user.setSuspended(true);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        assertThrows(DisabledException.class, () -> provider().authenticate(new UsernamePasswordAuthenticationToken("testuser", "password")));
    }
    
    @Test
    void testAuthenticationProviderWorks(){
        User user = new User("testuser", encoder.encode("password"), "test@example.com", "Java", 5, Role.ROLE_USER);
        user.setEnabled(true);
        user.setSuspended(false);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        Authentication auth = provider().authenticate(new UsernamePasswordAuthenticationToken("testuser", "password"));
        assertEquals(true,auth.isAuthenticated());
        assertEquals(user, auth.getPrincipal());
    }
}

