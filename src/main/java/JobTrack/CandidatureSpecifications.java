package JobTrack;

import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;

public class CandidatureSpecifications {
    
    public static Specification<Candidature> hasUser(User user){
        return (root,query,cb)-> 
            cb.equal(root.get("user"), user);
    }

    public static Specification<Candidature> hasStatut(Statut statut){
        return (root,query,cb)-> 
            cb.equal(root.get("statut"), statut);
    }

    public static Specification<Candidature> hasEntreprise(String entreprise){
       return (root, query, cb) ->
            cb.like(
                cb.lower(root.get("entreprise")),
                "%" + entreprise.toLowerCase() + "%"
            );
    }

    public static Specification<Candidature> before(LocalDate dateFin){
       return (root, query, cb) ->
            cb.lessThanOrEqualTo(root.get("date"),dateFin);
    }

    public static Specification<Candidature> after(LocalDate dateDebut){
        return (root, query, cb) ->
            cb.greaterThanOrEqualTo(root.get("date"),dateDebut);
    }

    public static Specification<Candidature> search(User user,Statut statut,String entreprise,LocalDate dateDebut,LocalDate dateFin){
        Specification<Candidature> spec = (root, query, cb) -> cb.conjunction();

        spec = spec.and(hasUser(user));

        if (statut != null) {
            spec = spec.and(hasStatut(statut));
        }

        if (entreprise != null && !entreprise.isBlank() ) {
            spec = spec.and(hasEntreprise(entreprise));
        }

        if(dateFin!=null){
            spec = spec.and(before(dateFin));
        }

        if(dateDebut!=null){
            spec = spec.and(after(dateDebut));
        }

        return spec;
    }
}
