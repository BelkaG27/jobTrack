package JobTrack;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<Fichier,Integer>{
    boolean existsByCandidatureAndTypeDocument(Candidature candidature,TypeDocument typeDocument);
    List<Fichier> findByCandidatureOrderByDateUploadDesc(Candidature candidature);
    Optional<Fichier> findByIdAndCandidature(int Id,Candidature candidature);
    List<Fichier> findByCandidature(Candidature candidature);
}
