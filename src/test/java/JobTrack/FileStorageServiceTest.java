package JobTrack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import JobTrack.Exceptions.FileEmptyException;
import JobTrack.Exceptions.FileStorageException;
import JobTrack.Exceptions.InvalidFileFormatException;
import JobTrack.Exceptions.InvalidPdfException;
import JobTrack.Exceptions.StoredFileNotFoundException;
import org.springframework.core.io.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;



@ExtendWith(MockitoExtension.class)
public class FileStorageServiceTest {

    @TempDir 
    private Path dir;

    @Mock
    private FileValidationService fileValidationService;

    private FileStorageService fileStorageService;
    
    @BeforeEach
    void setUp() throws IOException{
        fileStorageService = new FileStorageService(fileValidationService, dir.toString());
    }

    @Test
    void shouldStoreValidPdf() throws IOException{
        MockMultipartFile file = new MockMultipartFile("file","test.pdf","application/pdf","%PDF-hello".getBytes());

        String storageName = fileStorageService.storeFile(file);
        assertNotNull(storageName);
        assertTrue(storageName.endsWith(".pdf"));
        assertArrayEquals("%PDF-hello".getBytes(),Files.readAllBytes(dir.resolve(storageName)));

    }

    @Test 
    void shouldRejectEmptyFile()throws IOException{
        MockMultipartFile file = new MockMultipartFile("file","test.pdf","application/pdf",new byte[0]);

        doThrow(FileEmptyException.class).when(fileValidationService).isFileValid(file);

        assertThrows(FileEmptyException.class,()->fileStorageService.storeFile(file));
        Stream<Path> list = Files.list(dir);
        assertTrue(list.count()==0); list.close();
    }

    @Test 
    void shouldRejectNotPdfExtention()throws IOException{
        MockMultipartFile file = new MockMultipartFile("file","test.txt","text/plain","%PDF-hello".getBytes());

        doThrow(InvalidFileFormatException.class).when(fileValidationService).isFileValid(file);

        assertThrows(InvalidFileFormatException.class,()->fileStorageService.storeFile(file));
        Stream<Path> list = Files.list(dir);
        assertTrue(list.count()==0); list.close();
    }
    
    @Test 
    void shouldRejectFakePdfFile()throws IOException{
        MockMultipartFile file = new MockMultipartFile("file","test.pdf","application/pdf","hello".getBytes());

        doThrow(InvalidPdfException.class).when(fileValidationService).isFileValid(file);

        assertThrows(InvalidPdfException.class,()->fileStorageService.storeFile(file));
        Stream<Path> list = Files.list(dir);
        assertTrue(list.count()==0); list.close();
    }

    @Test 
    void shouldRejectPathTraversal()throws IOException{
        assertThrows(IllegalArgumentException.class,()->fileStorageService.loadFile("../fakePath"));
    }

    @Test 
    void noFileToLoad()throws IOException{
        assertThrows(StoredFileNotFoundException.class,()->fileStorageService.loadFile("blabla.pdf"));
    }

    @Test
    void loadsSuccessfully()throws IOException{
        MockMultipartFile file = new MockMultipartFile("file","test.pdf","application/pdf","%PDF-hello".getBytes());
        String storageName = fileStorageService.storeFile(file);

        Resource r = fileStorageService.loadFile(storageName);
        assertNotNull(r);

        try(InputStream input = r.getInputStream()){
            assertArrayEquals("%PDF-hello".getBytes(), input.readAllBytes());
        }
    }

    @Test 
    void deletedSuccessfully()throws IOException{
        MockMultipartFile file = new MockMultipartFile("file","test.pdf","application/pdf","%PDF-hello".getBytes());
        String storageName = fileStorageService.storeFile(file);
        
        try(Stream<Path> list = Files.list(dir)){
            assertTrue(list.count()==1);
        }
        
        fileStorageService.deleteFile(storageName);
        try(Stream<Path> list = Files.list(dir);){
            assertTrue(list.count()==0);
        }
    }


    @Test 
    void failsToStoreFile()throws IOException{
        MultipartFile file = mock(MultipartFile.class);
        
        when(file.getInputStream()).thenReturn(new InputStream() {
            private final byte[] data = "partial data".getBytes();
            private int index = 0;

            @Override
            public int read() throws IOException {
                if (index < data.length) {
                    return data[index++];
                }
                throw new IOException("Simulated read failure");
            }
        });

        assertThrows(FileStorageException.class, ()->fileStorageService.storeFile(file));

        try(Stream<Path> list = Files.list(dir)){
            assertTrue(list.count()==0);
        }
    }
}
