package JobTrack;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MailTokenRepository extends JpaRepository<MailToken,Integer> {
    
    public Optional<MailToken> findByTokenHash(String tokenHash);
    
    @Modifying(clearAutomatically = true)
    @Query("UPDATE MailToken m SET m.revoked = true WHERE m.user = :user")
    public void revokeByUser(@Param("user") User user);

    @Modifying 
    @Query("DELETE FROM MailToken m WHERE m.revoked=true OR m.expiryDate < CURRENT_TIMESTAMP")
    void deleteRevokedAndExpired();
}
