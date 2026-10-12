package JobTrack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import JobTrack.Exceptions.*;   

@Service 
public class FileValidationService {
    
    public void isFileValid(MultipartFile file)throws IOException, FileEmptyException, InvalidFileFormatException, InvalidPdfException {
        
        if(file==null || file.isEmpty()){ 
            throw new FileEmptyException("File is empty or null");
        }

        String name = file.getOriginalFilename();
        if(name==null || !name.toLowerCase().endsWith(".pdf")){
            throw new InvalidFileFormatException("Invalid file format. Only PDF files are allowed.");
        }

        try(InputStream inputStream = file.getInputStream()){
            byte[] header = inputStream.readNBytes(5); // Read the first 5 bytes of the file to check for the PDF header
            if(header.length < 5 || !Arrays.equals(header,"%PDF-".getBytes(StandardCharsets.US_ASCII))){
                throw new InvalidPdfException("Invalid PDF file.");
            }
        }
        catch(IOException e){
            throw new IOException("Error while validating the file ", e);
        }
        
        
    }
}

       
