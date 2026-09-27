package JobTrack;


import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

@Entity 
public class CandidatureStatusHistory{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull 
    private Statut ancienStatut;

    @NotNull 
    private Statut nouveauStatut;

    @NotNull 
    private LocalDateTime dateDeChangement;

    @ManyToOne
    @JoinColumn(name = "candidature_id")
    @JsonIgnore
    private Candidature candidature;



    public CandidatureStatusHistory(){ }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Statut getAncienStatut(){
        return ancienStatut;
    }

    public void setAncienStatut(Statut ancienStatut ){
        this.ancienStatut=ancienStatut;
    }

    public Statut getNouveauStatut(){
        return nouveauStatut;
    }

    public void setNouveauStatut(Statut nouveauStatut ){
        this.nouveauStatut=nouveauStatut;
    }

    public LocalDateTime getDateDeChangement(){
        return dateDeChangement;
    }

    public void setDateDeChangement(LocalDateTime dateDeChangement){
        this.dateDeChangement=dateDeChangement;
    }

    public Candidature getCandidature(){
        return candidature;
    }

    public void setCandidature(Candidature candidature){
        this.candidature=candidature;
    }
}
