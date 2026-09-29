package JobTrack;

import jakarta.validation.constraints.NotBlank;

public class ResendRequest {
    
    @NotBlank(message = "Le nom d'utilisateur ne peut pas être vide")
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
