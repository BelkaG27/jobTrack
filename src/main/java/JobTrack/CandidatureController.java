package JobTrack;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import JobTrack.Exceptions.CandidatureNotFoundException;
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
    public PageResponseDTO<CandidatureResponseDTO> getCandidatures(Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<Candidature> page = candidatures.findByUser(currentUser,pageable);
        return new PageResponseDTO<CandidatureResponseDTO>(page.getNumber(),page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast(), page.map(CandidatureResponseDTO::fromCandidature).toList());
    }

    @PostMapping("/candidatures")
    public ResponseEntity<CandidatureResponseDTO> addCandidature(@RequestBody @Valid CandidatureRequestDTO candidatureDTO) {
        User currentUser = getCurrentUser();

        Candidature candidature = candidatureDTO.toCandidature();
        candidature.setUser(currentUser);
        candidatures.save(candidature);
        return ResponseEntity.ok(CandidatureResponseDTO.fromCandidature(candidature));
    }

    @GetMapping("/candidatures/{id}")
    public ResponseEntity<CandidatureResponseDTO> getCandidatureById(@PathVariable int id){
        User currentUser = getCurrentUser();
        return candidatures.findById(id).filter(c->c.getUser().getId() == currentUser.getId()).map(CandidatureResponseDTO::fromCandidature).map(ResponseEntity::ok).orElseThrow(()-> new CandidatureNotFoundException("Candidature(s) not found"));
    }

    @PutMapping("/candidatures/{id}")
    public ResponseEntity<CandidatureResponseDTO> updateCandidature(@PathVariable int id, @RequestBody @Valid CandidatureRequestDTO updatedCandidature){
        User currentUser = getCurrentUser();
        Candidature existingCandidature = candidatures.findById(id).filter(c->c.getUser().getId() == currentUser.getId()).orElseThrow(()-> new CandidatureNotFoundException("Candidature(s) not found"));

        
            existingCandidature.setPoste(updatedCandidature.getPoste());
            existingCandidature.setEntreprise(updatedCandidature.getEntreprise());
            existingCandidature.setDate(updatedCandidature.getDate());
            existingCandidature.setLieu(updatedCandidature.getLieu());
            existingCandidature.setStatut(updatedCandidature.getStatut());
            
            candidatures.save(existingCandidature);
            return ResponseEntity.ok(CandidatureResponseDTO.fromCandidature(existingCandidature));
    }

    @DeleteMapping("/candidatures/{id}")
    public ResponseEntity<Void> deleteCandidature(@PathVariable int id){
        User currentUser = getCurrentUser();
        Candidature candidatureToDelete = candidatures.findById(id).filter(c->c.getUser().getId() == currentUser.getId()).orElseThrow(()-> new CandidatureNotFoundException("Candidature(s) not found"));

            candidatures.delete(candidatureToDelete);
            return ResponseEntity.noContent().build();
        

    }


}
