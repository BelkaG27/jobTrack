package JobTrack;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CandidatureRequestDTO {


     @NotBlank(message = "Le poste ne peut pas être vide")
    private   String poste;

    @NotBlank(message = "Le nom de l'entreprise ne peut pas être vide")
    private  String entreprise;

    @NotNull(message = "La date ne peut pas être vide")
    private  LocalDate date;

    @NotBlank(message = "Le lieu ne peut pas être vide")
    private  String lieu;
    
    @NotNull(message = "Le statut ne peut pas être vide")
    private   Statut statut;


    public CandidatureRequestDTO( String entreprise, LocalDate date, String lieu, Statut statut, String poste) {
        this.entreprise = entreprise;
        this.date = date;
        this.lieu = lieu;
        this.statut = statut;
        this.poste = poste;
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

    public  Candidature toCandidature(){

        return new Candidature(poste, entreprise, date, lieu, statut);
    }

    public static CandidatureRequestDTO fromCandidature(Candidature candidature){
        return new CandidatureRequestDTO(candidature.getEntreprise(), candidature.getDate(), candidature.getLieu(), candidature.getStatut(), candidature.getPoste());
    }

}
