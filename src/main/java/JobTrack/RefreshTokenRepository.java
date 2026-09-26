package JobTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Integer> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE RefreshToken r SET r.revoked = true WHERE r.user = :user")
    int revokeAllByUser(@Param("user") User user);

    public Optional<RefreshToken> findByTokenHash(String tokenHash);
    public List<RefreshToken> findByUserAndRevokedFalse(User user); 
    
    @Modifying 
    @Query("DELETE FROM RefreshToken r WHERE r.revoked=true OR r.expiryDate < CURRENT_TIMESTAMP")
    void deleteRevokedAndExpired();
}
