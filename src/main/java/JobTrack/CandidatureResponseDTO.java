package JobTrack;

import java.time.LocalDate;

public class CandidatureResponseDTO {
    private int id;
    private String entreprise;
    private LocalDate date;  
    private String lieu;
    private Statut statut;  
    private String poste;

    public CandidatureResponseDTO(int id, String entreprise, LocalDate date, String lieu, Statut statut, String poste) {
        this.id = id;
        this.entreprise = entreprise;
        this.date = date;
        this.lieu = lieu;
        this.statut = statut;
        this.poste = poste;
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

    public static CandidatureResponseDTO fromCandidature(Candidature candidature){
        return new CandidatureResponseDTO(candidature.getId(), candidature.getEntreprise(), candidature.getDate(), candidature.getLieu(), candidature.getStatut(), candidature.getPoste());
    }


}
