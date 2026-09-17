package JobTrack;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class LoginRequest {

    @NotBlank(message = "Le nom d'utilisateur ne peut pas être vide")
    @Pattern(regexp = "^(?!\\d+$)[A-Za-z0-9_]{3,20}$",
            // ^ / $ — couvre toute la chaîne .
            //( ?!   \d+    $ ) — negative lookahead : la chaîne ne doit pas être composée uniquement de chiffres.
            message = "Le nom d'utilisateur doit contenir au moins une lettre et ne peut pas être uniquement numérique, min 3 caractères")
    private String username;

    @NotBlank(message = "Le mot de passe ne peut pas être vide")
    private String password;

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
}