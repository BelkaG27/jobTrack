package JobTrack;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidatureStatusHistoryRepository extends JpaRepository<CandidatureStatusHistory, Integer> {
    List<CandidatureStatusHistory> findByCandidatureOrderByDateDeChangementDesc(Candidature candidature);
}
