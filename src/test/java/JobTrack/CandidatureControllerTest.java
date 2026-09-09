package JobTrack;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import JobTrack.Exceptions.CandidatureNotFoundException;
import JobTrack.Exceptions.GlobalExceptionHandler;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CandidatureControllerTest {

    @Mock
    CandidatureRepository candidatureRepository;

    @InjectMocks
    CandidatureController candidatureController;

    @Mock
    UserRepository userRepository;

    private User currentUser;

    private GlobalExceptionHandler globalHandler;

    @BeforeEach 
    public void setUp(){
        globalHandler = new GlobalExceptionHandler(); 
        currentUser = new User("bbk","bbk","smaili@gmail.com","java",5);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(currentUser,null,currentUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    @AfterEach 
    public void tearDown(){
        SecurityContextHolder.clearContext();
    }

    @Test 
    public void testGlobalExceptionHandler(){
        CandidatureNotFoundException exception = new CandidatureNotFoundException("Candidature not found test"); 
        ResponseEntity<String> response = globalHandler.handleCandidatureNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Candidature not found test", response.getBody());
    }


    @Test
    public void testGetCandidaturesById_Code200(){
        Candidature candidatureTest = new Candidature("Poste Test", "Entreprise Test", LocalDate.of(2026, 1, 1), "Lieu Test", Statut.EN_ATTENTE);
        candidatureTest.setId(1);
        candidatureTest.setUser(currentUser);
        when(userRepository.findByUsername(currentUser.getUsername())).thenReturn(Optional.of(currentUser));
        when(candidatureRepository.findById(1)).thenReturn(Optional.of(candidatureTest));

        ResponseEntity<CandidatureResponseDTO> response = candidatureController.getCandidatureById(1);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Poste Test", response.getBody().getPoste());
    }

    @Test
    public void testGetCandidaturesById_Code404(){
        when(candidatureRepository.findById(99)).thenReturn(Optional.empty());
        when(userRepository.findByUsername(currentUser.getUsername())).thenReturn(Optional.of(currentUser));

        assertThrows(CandidatureNotFoundException.class,()->candidatureController.getCandidatureById(99));
    }

    @Test
    public void testAddCandidature(){
        Candidature candidatureTest = new Candidature("Poste Test", "Entreprise Test", LocalDate.of(2026, 1, 1), "Lieu Test", Statut.EN_ATTENTE);
        when(candidatureRepository.save(any(Candidature.class))).thenReturn((candidatureTest));
        when(userRepository.findByUsername(currentUser.getUsername())).thenReturn(Optional.of(currentUser));
        
        ResponseEntity<CandidatureResponseDTO> response = candidatureController.addCandidature(CandidatureRequestDTO.fromCandidature(candidatureTest));
        assertEquals("Poste Test", response.getBody().getPoste());
        verify(candidatureRepository,times(1)).save(any(Candidature.class));
    }

    @Test
    public void testGetCandidatures(){
        Candidature candidatureTest1 = new Candidature("Poste Test", "Entreprise Test", LocalDate.of(2026, 1, 1), "Lieu Test", Statut.EN_ATTENTE);        
        Candidature candidatureTest2 = new Candidature("Poste Test", "Entreprise Test", LocalDate.of(2026, 1, 1), "Lieu Test", Statut.EN_ATTENTE);
        when(candidatureRepository.findByUser(currentUser)).thenReturn(List.of(candidatureTest1,candidatureTest2));
        when(userRepository.findByUsername(currentUser.getUsername())).thenReturn(Optional.of(currentUser));

        List<CandidatureResponseDTO> response = candidatureController.getCandidatures();
        assertEquals(2, response.size());
    }

}
