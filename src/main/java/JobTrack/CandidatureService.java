package JobTrack;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

@Component 
public class CandidatureService {
    @Autowired
    private CandidatureRepository candidatureRepository;

    @Scheduled(fixedRate = 259200000)
    @Transactional
    public void relanceCandidatures(){
        candidatureRepository.relanceCandidatures(LocalDate.now().minusDays(3), Statut.EN_ATTENTE);
    }


}
