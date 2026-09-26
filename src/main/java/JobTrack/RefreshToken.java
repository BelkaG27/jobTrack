package JobTrack;


import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;

@Entity
public class RefreshToken {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull 
    @Column(unique = true)
    private String tokenHash;

    @ManyToOne
    @JoinColumn(name="user_id") 
    private User user;

    @NotNull 
    private LocalDateTime expiryDate;

    private boolean revoked=false;

    private LocalDateTime createdAt = LocalDateTime.now();

    public RefreshToken(){};

    public RefreshToken(String tokenHash,User user,LocalDateTime expiryDate){
        this.tokenHash=tokenHash;
        this.user=user;
        this.expiryDate=expiryDate;
    }

     public int getId() {
        return id;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
