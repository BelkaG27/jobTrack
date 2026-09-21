package JobTrack;

import jakarta.validation.constraints.NotBlank;

public class RefreshRequest {
    
    @NotBlank(message="le champ token ne doit pas etre vide")
    private String token;

    public String getToken(){
        return token;
    }

    public void setToken(String token){
        this.token=token;
    }
}
