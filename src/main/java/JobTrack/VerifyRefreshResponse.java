package JobTrack;

public class VerifyRefreshResponse {
    private User user;
    private String refreshToken;

    
    public User getUser(){return user;}
    public String getRefreshToken(){return refreshToken;}

    public VerifyRefreshResponse(String token,User user){
        this.user = user;
        this.refreshToken=token;
    }
}
