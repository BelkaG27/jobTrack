package JobTrack;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import JobTrack.Exceptions.FileEmptyException;
import JobTrack.Exceptions.InvalidFileFormatException;
import JobTrack.Exceptions.InvalidPdfException;

public class FileValidationServiceTest {
    
    private FileValidationService fileValidationService;

    @BeforeEach 
    public void init(){
        fileValidationService = new FileValidationService();
    }

    @Test 
    public void fileEmpty()throws IOException{
        MockMultipartFile file = new MockMultipartFile("file","test.pdf","application/pdf",new byte[0]);

        assertThrows(FileEmptyException.class,()-> fileValidationService.isFileValid(file));
    }

    @Test 
    public void fileHadInvalidFormat()throws IOException{
        MockMultipartFile file = new MockMultipartFile("file","test.txt","text/plain","%PDF-hello".getBytes());

        assertThrows(InvalidFileFormatException.class,()-> fileValidationService.isFileValid(file));
    }

    @Test 
    public void fileIsNotPdf()throws IOException{
        MockMultipartFile file = new MockMultipartFile("file","test.pdf","application/pdf","%hello".getBytes());

        assertThrows(InvalidPdfException.class,()-> fileValidationService.isFileValid(file));
    }

    @Test 
    public void fileValid()throws IOException{
        MockMultipartFile file = new MockMultipartFile("file","test.pdf","application/pdf","%PDF-hello".getBytes());

        assertDoesNotThrow(()->fileValidationService.isFileValid(file));
    }
}
