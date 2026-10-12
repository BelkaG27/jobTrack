package JobTrack;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Fichier {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank 
    private String nomOrigine;

    @NotBlank 
    private String nomStockage;

    @NotBlank 
    private String typeMime;

    private long taille;

    @NotNull 
    private TypeDocument typeDocument;

    @NotNull 
    private LocalDateTime dateUpload;

    @ManyToOne
    @JoinColumn(name = "candidature_id")
    @JsonIgnore
    private Candidature candidature;

    public Fichier() { };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomOrigine() {
        return nomOrigine;
    }

    public void setNomOrigine(String nomOrigine) {
        this.nomOrigine = nomOrigine;
    }

    public String getNomStockage() {
        return nomStockage;
    }

    public void setNomStockage(String nomStockage) {
        this.nomStockage = nomStockage;
    }

    public String getTypeMime() {
        return typeMime;
    }

    public void setTypeMime(String typeMime) {
        this.typeMime = typeMime;
    }

    public long getTaille() {
        return taille;
    }

    public void setTaille(long taille) {
        this.taille = taille;
    }

    public TypeDocument getTypeDocument() {
        return typeDocument;
    }

    public void setTypeDocument(TypeDocument typeDocument) {
        this.typeDocument = typeDocument;
    }

    public LocalDateTime getDateUpload() {
        return dateUpload;
    }

    public void setDateUpload(LocalDateTime dateUpload) {
        this.dateUpload = dateUpload;
    }

    public Candidature getCandidature() {
        return candidature;
    }

    public void setCandidature(Candidature candidature) {
        this.candidature = candidature;
    }
}
