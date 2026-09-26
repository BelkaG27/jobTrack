package JobTrack;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface CandidatureRepository extends JpaRepository<Candidature, Integer> {
    public Page<Candidature> findByUser(User user,Pageable pageable);

    
    @Query("SELECT c.statut as statut, COUNT(c) AS nombre FROM Candidature c WHERE c.user = :user GROUP BY c.statut ")
    List<CandidatureStatutProjection> groupByStatut(@Param("user") User user);
}
