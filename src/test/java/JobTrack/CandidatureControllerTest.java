package JobTrack;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    @BeforeEach 
    public void setUp(){
        currentUser = new User("bbk","bbk","smaili@gmail.com","java",5);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(currentUser,null,currentUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        when(userRepository.findByUsername(currentUser.getUsername())).thenReturn(Optional.of(currentUser));
    }

    @AfterEach 
    public void tearDown(){
        SecurityContextHolder.clearContext();
    }


    @Test
    public void testGetCandidaturesById_Code200(){
        Candidature candidatureTest = new Candidature("Poste Test", "Entreprise Test", LocalDate.of(2026, 1, 1), "Lieu Test", Statut.EN_ATTENTE);
        candidatureTest.setId(1);
        candidatureTest.setUser(currentUser);
        when(candidatureRepository.findById(1)).thenReturn(Optional.of(candidatureTest));

        ResponseEntity<Candidature> response = candidatureController.getCandidatureById(1);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Poste Test", response.getBody().getPoste());
    }

    @Test
    public void testGetCandidaturesById_Code404(){
        when(candidatureRepository.findById(99)).thenReturn(Optional.empty());

        ResponseEntity<Candidature> response = candidatureController.getCandidatureById(99);
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    public void testAddCandidature(){
        Candidature candidatureTest = new Candidature("Poste Test", "Entreprise Test", LocalDate.of(2026, 1, 1), "Lieu Test", Statut.EN_ATTENTE);
        when(candidatureRepository.save(candidatureTest)).thenReturn((candidatureTest));

        ResponseEntity<Candidature> response = candidatureController.addCandidature(candidatureTest);
        assertEquals("Poste Test", response.getBody().getPoste());
        verify(candidatureRepository,times(1)).save(candidatureTest);
    }

    @Test
    public void testGetCandidatures(){
        Candidature candidatureTest1 = new Candidature("Poste Test", "Entreprise Test", LocalDate.of(2026, 1, 1), "Lieu Test", Statut.EN_ATTENTE);        
        Candidature candidatureTest2 = new Candidature("Poste Test", "Entreprise Test", LocalDate.of(2026, 1, 1), "Lieu Test", Statut.EN_ATTENTE);
        when(candidatureRepository.findByUser(currentUser)).thenReturn(List.of(candidatureTest1,candidatureTest2));

        List<Candidature> response = candidatureController.getCandidatures();
        assertEquals(2, response.size());
    }

}
