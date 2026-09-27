package JobTrack;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

@Component 
public class CandidatureService {
    @Autowired
    private CandidatureRepository candidatureRepository;

    @Autowired 
    private CandidatureStatusHistoryRepository candidatureStatusHistoryRepository;

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


}
