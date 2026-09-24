package JobTrack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import JobTrack.Exceptions.InvalidRefreshTokenException;

@ExtendWith(MockitoExtension.class)
public class RefreshTokensTest {
    
    @Mock 
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks 
    private RefreshTokenService refreshTokenService;

    @Test 
    public void testCreateRefreshToken()throws NoSuchAlgorithmException{
        User user = new User("bbk", "bbk", "smaili@gmail.com", "bbk", 1,Role.ROLE_ADMIN);
        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        String response = refreshTokenService.createRefreshToken(user); 
        
        verify(refreshTokenRepository, times(1)).save(captor.capture());
        RefreshToken rt = captor.getValue();

        assertEquals(rt.getUser(),user);
        assertEquals(rt.getTokenHash(),refreshTokenService.hashToken(response));
        assertFalse(rt.isRevoked());
        assertTrue(rt.getExpiryDate().isAfter(LocalDateTime.now()));

    } 

    @Test 
    public void testTokenNotFound()throws NoSuchAlgorithmException{
        String token = new String();
        when(refreshTokenRepository.findByTokenHash(refreshTokenService.hashToken(token))).thenReturn(Optional.empty());

        assertThrows(InvalidRefreshTokenException.class,()->refreshTokenService.verifyRefreshToken(token));
    }

    @Test
    public void testTokenRevoked()throws NoSuchAlgorithmException{
        String token = "blabla";
        RefreshToken rt = new RefreshToken(refreshTokenService.hashToken(token), new User(), LocalDateTime.now().plusMinutes(15));
        rt.setRevoked(true);
        when(refreshTokenRepository.findByTokenHash(refreshTokenService.hashToken(token))).thenReturn(Optional.of(rt));

        assertThrows(InvalidRefreshTokenException.class,()->refreshTokenService.verifyRefreshToken(token));
        verify(refreshTokenRepository,times(1)).revokeAllByUser(any(User.class));
    } 

    @Test
    public void testTokenExpired()throws NoSuchAlgorithmException{
        String token = "blabla";
        RefreshToken rt = new RefreshToken(refreshTokenService.hashToken(token), new User(), LocalDateTime.now().minusMinutes(15));
        rt.setRevoked(false);
        when(refreshTokenRepository.findByTokenHash(refreshTokenService.hashToken(token))).thenReturn(Optional.of(rt));

        assertThrows(InvalidRefreshTokenException.class,()->refreshTokenService.verifyRefreshToken(token));
    } 

    @Test 
    public void testTokenSuccess()throws NoSuchAlgorithmException{
        User user =  new User("bbk", "bbk", "smaili@gmail.com", "bbk", 1,Role.ROLE_ADMIN);
        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        String token = "blabla";
        RefreshToken rt = new RefreshToken(refreshTokenService.hashToken(token), user, LocalDateTime.now().plusMinutes(15));
        rt.setRevoked(false);
        when(refreshTokenRepository.findByTokenHash(refreshTokenService.hashToken(token))).thenReturn(Optional.of(rt));

        VerifyRefreshResponse response = refreshTokenService.verifyRefreshToken(token);
        
        verify(refreshTokenRepository,times(2)).save(captor.capture());
        List<RefreshToken> refreshToken = captor.getAllValues();
        
        assertEquals(response.getUser(),user);
        assertEquals(refreshToken.get(0).isRevoked(), true);
        assertEquals(refreshToken.get(1).isRevoked(), false);
    }

    @Test 
    public void testLogoutTokenRevoked()throws NoSuchAlgorithmException{
        String token = "blabla";
        RefreshToken rt = new RefreshToken(refreshTokenService.hashToken(token), new User(), LocalDateTime.now().plusMinutes(15));
        rt.setRevoked(true);
        when(refreshTokenRepository.findByTokenHash(refreshTokenService.hashToken(token))).thenReturn(Optional.of(rt));

        assertThrows(InvalidRefreshTokenException.class,()->refreshTokenService.logout(token));
        verify(refreshTokenRepository,times(1)).revokeAllByUser(any(User.class));
    }

    @Test
    public void testLogoutTokenExpired()throws NoSuchAlgorithmException{
        String token = "blabla";
        RefreshToken rt = new RefreshToken(refreshTokenService.hashToken(token), new User(), LocalDateTime.now().minusMinutes(15));
        rt.setRevoked(false);
        when(refreshTokenRepository.findByTokenHash(refreshTokenService.hashToken(token))).thenReturn(Optional.of(rt));

        assertThrows(InvalidRefreshTokenException.class,()->refreshTokenService.logout(token));
    }

    @Test
    public void testLogoutSuccess()throws NoSuchAlgorithmException{
        String token = "blabla";
        RefreshToken rt = new RefreshToken(refreshTokenService.hashToken(token), new User(), LocalDateTime.now().plusMinutes(15));
        rt.setRevoked(false);
        when(refreshTokenRepository.findByTokenHash(refreshTokenService.hashToken(token))).thenReturn(Optional.of(rt));

        refreshTokenService.logout(token);
        verify(refreshTokenRepository,times(1)).revokeAllByUser(any(User.class));
    }
}
