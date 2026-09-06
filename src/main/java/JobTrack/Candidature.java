package JobTrack;

import java.time.LocalDate;

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
public class Candidature {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    
    @NotBlank(message = "Le poste ne peut pas être vide")
    private String poste;

    @NotBlank(message = "Le nom de l'entreprise ne peut pas être vide")
    private String entreprise;

    @NotNull(message = "La date ne peut pas être vide")
    private LocalDate date;

    @NotBlank(message = "Le lieu ne peut pas être vide")
    private  String lieu;
    
    @NotNull(message = "Le statut ne peut pas être vide")
    private Statut statut;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Candidature( String poste, String entreprise, LocalDate date, String lieu, Statut statut) {

        this.poste = poste;
        this.entreprise = entreprise;
        this.date = date;
        this.lieu = lieu;
        this.statut = statut;
    }

    public Candidature() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPoste() {
        return poste;
    }

    public void setPoste(String poste) {
        this.poste = poste;
    }

    public String getEntreprise() {
        return entreprise;
    }

    public void setEntreprise(String entreprise) {
        this.entreprise = entreprise;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }


}
