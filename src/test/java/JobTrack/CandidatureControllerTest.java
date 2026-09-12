package JobTrack;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import JobTrack.Exceptions.CandidatureNotFoundException;
import JobTrack.Exceptions.EmailAlreadyExistsException;
import JobTrack.Exceptions.GlobalExceptionHandler;
import JobTrack.Exceptions.UsernameAlreadyExistsException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
    public void testHandleCandidatureNotFound(){
        CandidatureNotFoundException exception = new CandidatureNotFoundException("Candidature not found test"); 
        ResponseEntity<String> response = globalHandler.handleCandidatureNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Candidature not found test", response.getBody());
    }

    @Test
    public void testUsernameAlreadyExists(){
        UsernameAlreadyExistsException exception = new UsernameAlreadyExistsException("Username already exists test");
        ResponseEntity<String> response = globalHandler.handleUsernameAlredyExists(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Username already exists test", response.getBody());
    }

    @Test
    public void testEmailAlreadyExists(){
        EmailAlreadyExistsException exception = new EmailAlreadyExistsException("Email already exists test");
        ResponseEntity<String> response = globalHandler.handleEmailAlredyExists(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Email already exists test", response.getBody());
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

    public static CandidatureRequestDTO candidatureToDTO(Candidature candidature){
        return new CandidatureRequestDTO(candidature.getEntreprise(), candidature.getDate(), candidature.getLieu(), candidature.getStatut(), candidature.getPoste());
    }

    @Test
    public void testAddCandidature(){
        Candidature candidatureTest = new Candidature("Poste Test", "Entreprise Test", LocalDate.of(2026, 1, 1), "Lieu Test", Statut.EN_ATTENTE);
        when(candidatureRepository.save(any(Candidature.class))).thenReturn((candidatureTest));
        when(userRepository.findByUsername(currentUser.getUsername())).thenReturn(Optional.of(currentUser));
        
        ResponseEntity<CandidatureResponseDTO> response = candidatureController.addCandidature(candidatureToDTO(candidatureTest));
        assertEquals("Poste Test", response.getBody().getPoste());
        verify(candidatureRepository,times(1)).save(any(Candidature.class));
    }

    @Test
    public void testGetCandidatures(){
        Candidature candidatureTest1 = new Candidature("Poste Test", "Entreprise Test", LocalDate.of(2026, 1, 1), "Lieu Test", Statut.EN_ATTENTE);        
        Candidature candidatureTest2 = new Candidature("Poste Test", "Entreprise Test", LocalDate.of(2026, 1, 1), "Lieu Test", Statut.EN_ATTENTE);
        when(candidatureRepository.findByUser(any(User.class), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(candidatureTest1,candidatureTest2)));
        when(userRepository.findByUsername(currentUser.getUsername())).thenReturn(Optional.of(currentUser));

        PageResponseDTO<CandidatureResponseDTO> response = candidatureController.getCandidatures(PageRequest.of(0,10));
        assertEquals(2, response.getContent().size());
    }

    @Test 
    public void testHandleValidationErrors(){
        FieldError fieldError = new FieldError("candidatureRequestDTO", "poste", "Le poste ne peut pas être vide");
        BindingResult bindingResult = new BeanPropertyBindingResult(new CandidatureRequestDTO(),"candidatureRequestDTO");
        bindingResult.addError(fieldError);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String,String>> response = globalHandler.handleValidationErrors(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Le poste ne peut pas être vide", response.getBody().get("poste"));

    }

}
