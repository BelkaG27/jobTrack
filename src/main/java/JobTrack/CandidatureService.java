package JobTrack;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

@Component 
public class CandidatureService {
    @Autowired
    private CandidatureRepository candidatureRepository;

    @Autowired 
    private CandidatureStatusHistoryRepository candidatureStatusHistoryRepository;

    @Autowired 
    private FileService fileService;

    @Autowired 
    private FileRepository fileRepository;

    @Scheduled(fixedRate = 259200000)
    @Transactional
    public void relanceCandidatures(){
        candidatureRepository.relanceCandidatures(LocalDate.now().minusDays(3), Statut.EN_ATTENTE);
    }

    public void createNewStatutHistory(Candidature existing,CandidatureRequestDTO updated){
        CandidatureStatusHistory history = new CandidatureStatusHistory();
        history.setAncienStatut(existing.getStatut());
        history.setNouveauStatut(updated.getStatut());
        history.setCandidature(existing);
        history.setDateDeChangement(LocalDateTime.now());
        candidatureStatusHistoryRepository.save(history);
    }

    @Transactional 
    public void deleteCandidature(Candidature candidature){
        List<String> liste = fileRepository.findByCandidature(candidature).stream().map(Fichier::getNomStockage).toList();
        candidatureRepository.delete(candidature);
        fileService.supprimerAfterCommit(liste);
    }

    public Page<Candidature> searchCandidatureBy(User user, Statut statut,String entreprise,LocalDate dateDebut,LocalDate dateFin,Pageable page){
        Specification<Candidature> spec = CandidatureSpecifications.search(user, statut,entreprise,dateDebut,dateFin);
        
        return candidatureRepository.findAll(spec, page);
    }


}
