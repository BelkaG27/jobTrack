package JobTrack;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidatureRepository extends JpaRepository<Candidature, Integer> {
    public List<Candidature> findByUser(User user);
}
