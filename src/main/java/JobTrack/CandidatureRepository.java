package JobTrack;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;



public interface CandidatureRepository extends JpaRepository<Candidature, Integer> {
    public Page<Candidature> findByUser(User user,Pageable pageable);
}
