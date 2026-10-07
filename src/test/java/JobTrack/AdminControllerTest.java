package JobTrack;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import JobTrack.Exceptions.ChangeSuspendedException;
import JobTrack.Exceptions.UserNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    @Mock
    UserRepository userRepository;

    @Mock 
    RefreshTokenRepository refreshTokenRepository;

    private User currentUser;

    @InjectMocks 
    private UserService userService;

    @BeforeEach 
    public void setUp(){
        currentUser = new User("bbk","bbk","smaili@gmail.com","java",5,Role.ROLE_ADMIN);
        currentUser.setEnabled(true);
        currentUser.setId(1);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(currentUser,null,currentUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    @AfterEach 
    public void tearDown(){
        SecurityContextHolder.clearContext();
    }

    @Test 
    public void testUpdateUserSuspendedTrue(){
        User bob = new User("bob","bob","bob@gmail.com","java",5,Role.ROLE_USER);
        bob.setEnabled(true);
        bob.setId(2);
        when(userRepository.findByUsername(currentUser.getUsername())).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(2)).thenReturn(Optional.of(bob));

        UserResponseDTO response = userService.updateUserSuspended(2, true);
        assertTrue(response.getSuspended());
        verify(userRepository,times(1)).saveAndFlush(any(User.class));
        verify(refreshTokenRepository,times(1)).revokeAllByUser(bob);
    }

    @Test 
    public void testUpdateUserSuspendedFalse(){
        User bob = new User("bob","bob","bob@gmail.com","java",5,Role.ROLE_USER);
        bob.setEnabled(true);
        bob.setId(2);
        when(userRepository.findByUsername(currentUser.getUsername())).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(2)).thenReturn(Optional.of(bob));

        UserResponseDTO response = userService.updateUserSuspended(2, false);
        assertFalse(response.getSuspended());
        verify(userRepository,times(1)).saveAndFlush(any(User.class));
        verify(refreshTokenRepository,never()).revokeAllByUser(bob);
    }

    @Test 
    public void testUpdateUserSuspendedUserNotFound(){
        when(userRepository.findByUsername(currentUser.getUsername())).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,()->userService.updateUserSuspended(99, true));
    }

    @Test 
    public void testUpdateUserSuspendedWithAdminId(){
        when(userRepository.findByUsername(currentUser.getUsername())).thenReturn(Optional.of(currentUser));

        assertThrows(ChangeSuspendedException.class,()->userService.updateUserSuspended(1, true));
        verify(userRepository, never()).saveAndFlush(any(User.class));
        verify(refreshTokenRepository, never()).revokeAllByUser(any(User.class));
    }
}
