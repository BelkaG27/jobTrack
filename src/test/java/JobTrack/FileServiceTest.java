package JobTrack;

import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockMultipartFile;
import JobTrack.Exceptions.FileTypeAlreadyExistsException;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {
    
    @Mock     
    private FileRepository fileRepository;

    @Mock 
    private FileStorageService fileStorageService;

    @Mock 
    private CandidatureRepository candidatureRepository;

    @Mock 
    private UserService userService;
    
    @InjectMocks 
    private FileService fileService;


    @Test 
    public void testFileSaveFailsAndDeletesStored()throws IOException{
        MockMultipartFile file = new MockMultipartFile("file","test.pdf","application/pdf","%PDF-hello".getBytes()); 
        User current = new User(); current.setId(1);
        Candidature candidature = new Candidature(); candidature.setId(1); candidature.setUser(current); 
        
        doThrow(DataIntegrityViolationException.class).when(fileRepository).save(any(Fichier.class));
        
        when(userService.getCurrentUser()).thenReturn(current);
        when(candidatureRepository.findById(1)).thenReturn(Optional.of(candidature));
        when(fileRepository.existsByCandidatureAndTypeDocument(candidature,TypeDocument.CV)).thenReturn(false);
        when(fileStorageService.storeFile(file)).thenReturn("blabla.pdf");
        
        assertThrows(FileTypeAlreadyExistsException.class,()->fileService.uploadFile(file,1,TypeDocument.CV));

        verify(fileStorageService).deleteFile("blabla.pdf");
    }


    @Test 
    public void deleteFileAfterCommit(){
        Fichier file = new Fichier(); file.setId(1); file.setNomStockage("blabla.pdf");
        User current = new User(); current.setId(1);
        Candidature candidature = new Candidature(); candidature.setId(1); candidature.setUser(current);

        when(userService.getCurrentUser()).thenReturn(current);
        when(candidatureRepository.findById(1)).thenReturn(Optional.of(candidature));
        when(fileRepository.findByIdAndCandidature(1,candidature)).thenReturn(Optional.of(file));


        TransactionSynchronizationManager.initSynchronization();
        fileService.deleteFile(1, 1);
        verify(fileStorageService,times(0)).deleteFile("blabla.pdf");
        TransactionSynchronizationManager.getSynchronizations().get(0).afterCommit();
        verify(fileStorageService,times(1)).deleteFile("blabla.pdf");
        TransactionSynchronizationManager.clearSynchronization();
    }
}
