package JobTrack;

import java.time.LocalDate;

public class CandidatureResponseDTO {
    private int id;
    private String entreprise;
    private LocalDate date;
    private LocalDate derniereMisAJour;
    private String lieu;
    private Statut statut;  
    private String poste;
    private Boolean relanceRecommandee; 

    public CandidatureResponseDTO(int id, String entreprise, LocalDate date, String lieu, Statut statut, String poste,Boolean relanceRecommandee,LocalDate derniereMisAJour) {
        this.id = id;
        this.entreprise = entreprise;
        this.date = date;
        this.lieu = lieu;
        this.statut = statut;
        this.poste = poste;
        this.relanceRecommandee=relanceRecommandee;
        this.derniereMisAJour=derniereMisAJour;
    }

    public int getId() {
        return id;
    }


    public String getEntreprise() {
        return entreprise;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getLieu() {
        return lieu;
    }

    public Statut getStatut() {
        return statut;
    }

    public String getPoste() {
        return poste;
    }

    public Boolean getRelanceRecommandee(){
        return relanceRecommandee;
    }

    public LocalDate getDerniereMisAJour() {
        return derniereMisAJour;
    }

    public static CandidatureResponseDTO fromCandidature(Candidature candidature){
        return new CandidatureResponseDTO(candidature.getId(), candidature.getEntreprise(), candidature.getDate(), candidature.getLieu(), candidature.getStatut(), candidature.getPoste(),candidature.getRelanceRecommandee(),candidature.getDerniereMisAJour());
    }


}
