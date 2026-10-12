package JobTrack;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import JobTrack.Exceptions.CandidatureNotFoundException;
import JobTrack.Exceptions.FileNotFoundException;
import JobTrack.Exceptions.FileTypeAlreadyExistsException;
import JobTrack.Exceptions.StoredFileNotFoundException;
import org.springframework.transaction.support.TransactionSynchronization;


import org.springframework.core.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service 
public class FileService {
    
    @Autowired 
    private FileStorageService fileStorageService;

    @Autowired 
    private FileRepository fileRepository;

    @Autowired 
    private CandidatureRepository candidatureRepository;

    @Autowired 
    private UserService userService;

    private static Logger logger = LoggerFactory.getLogger(FileService.class);
    
    private Candidature getCandidatureByIDAndVerifyUser(int candidatureId){
        User current = userService.getCurrentUser();
        Candidature candidature = candidatureRepository.findById(candidatureId).orElseThrow(()-> new CandidatureNotFoundException("Candidature(s) not found"));
        if(candidature.getUser().getId() != current.getId()){
            throw new CandidatureNotFoundException("Candidature not found !");
        }
        return candidature;
    }

    public FileResponseDTO uploadFile(MultipartFile file,int candidatureId,TypeDocument type)throws IOException{
        Candidature candidature = getCandidatureByIDAndVerifyUser(candidatureId);
        if(fileRepository.existsByCandidatureAndTypeDocument(candidature, type)){
            throw new FileTypeAlreadyExistsException("cette candidature a deja ce type de fichier :" + type.toString());
        }

        String nomStockage = fileStorageService.storeFile(file);

        Fichier fichier = new Fichier();
        fichier.setCandidature(candidature);
        fichier.setDateUpload(LocalDateTime.now());
        
        String nomOrigine = StringUtils.cleanPath(file.getOriginalFilename());
        nomOrigine = StringUtils.getFilename(nomOrigine);
        fichier.setNomOrigine(nomOrigine);
        
        fichier.setTaille(file.getSize());
        fichier.setTypeDocument(type);
        fichier.setTypeMime("application/pdf");
        fichier.setNomStockage(nomStockage);
        try{
            fileRepository.save(fichier);
        }
        catch(DataIntegrityViolationException e){
            fileStorageService.deleteFile(nomStockage);
            throw new FileTypeAlreadyExistsException("cette candidature a deja ce type de fichier :" + type.toString());
        }
        catch(RuntimeException e){
            fileStorageService.deleteFile(nomStockage);
            throw e;
        }
        

        return FileResponseDTO.fromFichier(fichier);
    }

    public List<FileResponseDTO> listFiles(int candidatureID){
        Candidature candidature = getCandidatureByIDAndVerifyUser(candidatureID);
        List<Fichier> liste = fileRepository.findByCandidatureOrderByDateUploadDesc(candidature);
        List<FileResponseDTO> listeDTO = new ArrayList<FileResponseDTO>();

        for (Fichier fichier : liste) {
            listeDTO.add(FileResponseDTO.fromFichier(fichier));
        }

        return listeDTO;
    }

    public FileLoadResponseDTO loadFile(int candidatureId,int fileId) throws IOException{
        Candidature candidature = getCandidatureByIDAndVerifyUser(candidatureId);
        Fichier fichier = fileRepository.findByIdAndCandidature(fileId,candidature).orElseThrow(() -> new FileNotFoundException("File not found !"));
        
        try{
            Resource r = fileStorageService.loadFile(fichier.getNomStockage());
            return new FileLoadResponseDTO(r, fichier.getNomOrigine(), fichier.getTypeMime());
        }
        catch(StoredFileNotFoundException e){
            logger.warn("Stored file missing for fichier id={}",fichier.getId());
            throw e;
        }
    }

    public void supprimerAfterCommit(List<String> noms){
        if(noms==null||noms.size()==0){
            return;
        }
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException(
                "Aucune synchronisation transactionnelle active"
            );
        }
        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    for(String string : noms){
                        try{
                            fileStorageService.deleteFile(string);
                        }catch(RuntimeException e){
                            logger.warn("erreur lors de la suppression du fichier ={}",string,e);
                        }
                    }
                    
                }
        });
    }

    @Transactional 
    public void deleteFile(int candidatureId,int fileId){
        Candidature candidature = getCandidatureByIDAndVerifyUser(candidatureId);
        Fichier fichier = fileRepository.findByIdAndCandidature(fileId,candidature).orElseThrow(() -> new FileNotFoundException("File not found !"));

        String nomStockage = fichier.getNomStockage(); 
        fileRepository.delete(fichier);

        supprimerAfterCommit(List.of(nomStockage));
        
    }
}
