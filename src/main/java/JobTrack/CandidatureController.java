package JobTrack;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class CandidatureController {
    
    
    @Autowired
    private CandidatureRepository candidatures;

    @Autowired 
    private UserRepository users;

    private User getCurrentUser(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        return users.findByUsername(name).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/candidatures")
    public List<CandidatureResponseDTO> getCandidatures() {
        User currentUser = getCurrentUser();
        List<Candidature> liste = candidatures.findByUser(currentUser);
        return liste.stream().map(CandidatureResponseDTO::fromCandidature).toList();
    }

    @PostMapping("/candidatures")
    public ResponseEntity<Candidature> addCandidature(@RequestBody @Valid Candidature candidature) {
        User currentUser = getCurrentUser();
        candidature.setUser(currentUser);
        candidatures.save(candidature);
        return ResponseEntity.ok(candidature);
    }

    @GetMapping("/candidatures/{id}")
    public ResponseEntity<Candidature> getCandidatureById(@PathVariable int id){
        // Candidature candidature = candidatures.stream()
        //         .filter(c->c.getId() == id)
        //         .findFirst().orElse(null);

        // if(candidature != null){
        //     return ResponseEntity.ok(candidature);
        // } else {
        //     return ResponseEntity.notFound().build();
        // }

        User currentUser = getCurrentUser();
        return candidatures.findById(id).filter(c->c.getUser().getId() == currentUser.getId()).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/candidatures/{id}")
    public ResponseEntity<Candidature> updateCandidature(@PathVariable int id, @RequestBody @Valid Candidature updatedCandidature){
        User currentUser = getCurrentUser();
        Candidature existingCandidature = candidatures.findById(id).filter(c->c.getUser().getId() == currentUser.getId()).orElse(null);

        if(existingCandidature != null){
            existingCandidature.setPoste(updatedCandidature.getPoste());
            existingCandidature.setEntreprise(updatedCandidature.getEntreprise());
            existingCandidature.setDate(updatedCandidature.getDate());
            existingCandidature.setLieu(updatedCandidature.getLieu());
            existingCandidature.setStatut(updatedCandidature.getStatut());
            
            candidatures.save(existingCandidature);
            return ResponseEntity.ok(existingCandidature);
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/candidatures/{id}")
    public ResponseEntity<Void> deleteCandidature(@PathVariable int id){
        User currentUser = getCurrentUser();
        Candidature candidatureToDelete = candidatures.findById(id).filter(c->c.getUser().getId() == currentUser.getId()).orElse(null);

        if(candidatureToDelete != null){
            candidatures.delete(candidatureToDelete);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }


}
