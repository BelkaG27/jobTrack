package JobTrack;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.InjectableValues.Base;

import JobTrack.Exceptions.InvalidRefreshTokenException;

 
@Service 
public class RefreshTokenService {
    
    @Autowired 
    private RefreshTokenRepository refreshTokenRepo;


    public String hashToken(String token)throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance("SHA-256"); // on hash utilisant l'algorithme "SHA-256"
        byte[]bytesApresHash=md.digest(token.getBytes(StandardCharsets.UTF_8)); // on hash les bytes du token(string)

        return Base64.getEncoder().encodeToString(bytesApresHash); // on encode vers un string qu'on stock dans la BD (token hashé)
    }

    public String createRefreshToken(User user)throws NoSuchAlgorithmException{
        SecureRandom sr = new SecureRandom(); 
        byte[] bytes = new byte[16]; 
        sr.nextBytes(bytes); // generer des octets aleatoires
        
        String encoder = Base64.getEncoder().encodeToString(bytes);  // encoder en chaine de caractères

        String encodeHash = hashToken(encoder);
        RefreshToken rt = new RefreshToken(encodeHash, user, LocalDateTime.now().plusDays(7));

        refreshTokenRepo.save(rt);
        return encoder;
    }

    @Transactional
    public VerifyRefreshResponse verifyRefreshToken(String token)throws NoSuchAlgorithmException,InvalidRefreshTokenException{
        RefreshToken rt = refreshTokenRepo.findByTokenHash(hashToken(token)).orElseThrow(()->new InvalidRefreshTokenException("le token saisie n'existe pas !"));
        if(rt.isRevoked()){
            refreshTokenRepo.revokeAllByUser(rt.getUser());
            throw new InvalidRefreshTokenException("ce token a déja été utilisé !");
        }
        if(rt.getExpiryDate().isBefore(LocalDateTime.now())){
            throw new InvalidRefreshTokenException("ce token a expiré !");
        }

        rt.setRevoked(true);
        refreshTokenRepo.save(rt);

        return ( new VerifyRefreshResponse(createRefreshToken(rt.getUser()),rt.getUser()));

    }

    @Scheduled(fixedRate = 900000)
    public void nettoyerRefreshRepo(){
        refreshTokenRepo.deleteRevokedAndExpired();
    }
}
