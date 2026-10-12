package JobTrack;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.core.io.Resource;

@RestController 
public class FileController {
    
    @Autowired 
    private FileService fileService;

    @PostMapping(value = "/candidatures/{id}/files",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)  
    public ResponseEntity<FileResponseDTO> addFileToCandidature(@PathVariable int id,@RequestParam("file") MultipartFile file,@RequestParam("typeDocument") TypeDocument typeDocument)throws IOException{
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(fileService.uploadFile(file, id, typeDocument));
    }

    @GetMapping(value = "/candidatures/{id}/files")  
    public ResponseEntity<List<FileResponseDTO>> listFiles(@PathVariable int id)throws IOException{
        return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(fileService.listFiles(id));
    }

    @GetMapping(value = "/candidatures/{id}/files/{fileId}")  
    public ResponseEntity<Resource> downloadFile(@PathVariable int id,@PathVariable int fileId)throws IOException{
        FileLoadResponseDTO file = fileService.loadFile(id,fileId);
        return ResponseEntity.status(HttpStatusCode.valueOf(200))
                .contentType(MediaType.parseMediaType(file.getTypeMime()))
                .contentLength(file.getResource().contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment() // demande au navigateur de télécharger le fichier
                        .filename(file.getNomOrigine(), StandardCharsets.UTF_8) 
                        .build()
                        .toString()
                )
                .body(file.getResource());
    }

    @DeleteMapping(value = "/candidatures/{id}/files/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable int id,@PathVariable int fileId){
        fileService.deleteFile(id, fileId);
        return ResponseEntity.noContent().build();
    }
}
