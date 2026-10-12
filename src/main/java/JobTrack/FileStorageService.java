package JobTrack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import JobTrack.Exceptions.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// import org.springframework.transaction.support.TransactionSynchronizationManager;
// import org.springframework.transaction.support.TransactionSynchronization;
// import java.util.List;

@Service 
public class FileStorageService {
     
    private FileValidationService fileValidationService;

    private Path uploadDir;

    private static Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    public FileStorageService(FileValidationService fileValidationService,@Value ("${file.storage.dir}") String path) throws IOException{
        
        this.fileValidationService = fileValidationService;
        uploadDir = Path.of(path).toAbsolutePath().normalize(); // Normalize the path to prevent path traversal attacks
        
        Files.createDirectories(uploadDir); // Create the upload directory if it doesn't exist
    }

    // This method will store the file in the specified directory after validating it
    public String storeFile(MultipartFile file)throws IOException {
        fileValidationService.isFileValid(file); // Validate the file before storing it
        String newFileName = UUID.randomUUID().toString() + ".pdf"; // Generate a unique file name to avoid overwriting existing files

        Path targetLocation = uploadDir.resolve(newFileName).normalize(); // Resolve the target location for the file

        if(!targetLocation.startsWith(uploadDir)){ // Check if the target location is within the upload directory to prevent path traversal attacks
            throw new FileStorageException("Cannot store file outside the current directory.", null);
        }

        try(InputStream inputStream = file.getInputStream()){ // generate an InputStream from the MultipartFile and copy it to the target location
            Files.copy(inputStream, targetLocation);
            return newFileName;
        } catch (IOException e) {
            try{
                Files.deleteIfExists(targetLocation); // Delete the file if it was partially written
            } catch (IOException ex) {
                e.addSuppressed(ex);
            }
            throw new FileStorageException("Error while storing file", e);
        }     
    }

    // This method will load a file from the specified path
    public Resource loadFile(String nomStockage)throws IOException{
        Path filePath = uploadDir.resolve(nomStockage).normalize(); // load the file from the specified path and normalize it to prevent path traversal attacks
        if(!filePath.startsWith(uploadDir)){ // Check if the file path is within the upload directory to prevent path traversal attacks
            throw new IllegalArgumentException("Invalid file path");
        }
        
        Resource resource = new UrlResource(filePath.toUri()); // Create a UrlResource from the file path

        if(resource.exists() && resource.isReadable()){
            return resource;
        }
        throw new StoredFileNotFoundException("File not found or not readable: " + nomStockage);
    }


    public void deleteFile(String nomStockage){
        Path filePath = uploadDir.resolve(nomStockage).normalize(); // load the file from the specified path and normalize it to prevent path traversal attacks
        if(!filePath.startsWith(uploadDir)){ // Check if the file path is within the upload directory to prevent path traversal attacks
            throw new RuntimeException("Cannot delete file outside the current directory.");
        }
        try{
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            logger.warn("Could not delete stored file: {}", nomStockage, e);
        }
    }

    // public void supprimerAfterCommit(List<String> noms){
    //     for (String string : noms) {
    //         TransactionSynchronizationManager.registerSynchronization(
    //             new TransactionSynchronization() {
    //                 @Override
    //                 public void afterCommit() {
    //                     try{
    //                         deleteFile(string);
    //                     }catch(RuntimeException e){
    //                         logger.warn("erreur lors de la suppression du fichier ={}",string,e);
    //                     }
    //                 }
    //         });
    //     }
    // }


}
