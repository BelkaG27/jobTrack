package JobTrack;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class RegisterRequest {

    @NotBlank(message = "Le nom d'utilisateur ne peut pas être vide")
    @Pattern(regexp = "^(?!\\d+$)[A-Za-z0-9_]{3,20}$",
            // ^ / $ — couvre toute la chaîne .
            //( ?!   \d+    $ ) — negative lookahead : la chaîne ne doit pas être composée uniquement de chiffres.
            message = "Le nom d'utilisateur doit contenir au moins une lettre et ne peut pas être uniquement numérique,min 3 caractères")
    private String username;

    @NotBlank(message = "Le mot de passe ne peut pas être vide")
    private String password;

    @NotBlank(message = "l'email ne peut pas être vide")  // a verifier
    @Email(message = "l'email n'est pas valide")
    private String email;

    @NotBlank(message = "La specialité ne peut pas être vide")
    private String specialty;

    @NotNull(message ="le nombre d'années d'expérience ne peut pas être vide")
    private int yearsOfExperience;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }



}