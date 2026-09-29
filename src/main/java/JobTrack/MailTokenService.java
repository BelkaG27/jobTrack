package JobTrack;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import JobTrack.Exceptions.InvalidRefreshTokenException;

@Service 
public class MailTokenService {

    @Autowired 
    private MailService mailService;

    @Autowired 
    private UserRepository userRepository;

    @Autowired 
    private MailTokenRepository mailTokenRepository;

    public String hashToken(String token)throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance("SHA-256"); 
        byte[]bytesApresHash=md.digest(token.getBytes(StandardCharsets.UTF_8)); 

        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytesApresHash); 
    }


    @Value("${mail.link}")
    private String mailLink;

    @Transactional 
    public void createMailTokenAndSendMail(User user)throws NoSuchAlgorithmException{
        SecureRandom sr = new SecureRandom(); 
        byte[] bytes = new byte[16]; 
        sr.nextBytes(bytes); 
        
        String encoder = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);  

        String encodeHash = hashToken(encoder);
        MailToken mt = new MailToken(encodeHash, user, LocalDateTime.now().plusMinutes(10));

        mailTokenRepository.revokeByUser(user);

        mailTokenRepository.save(mt);
        
        mailService.sendSimpleEmail("admin@gmail.com", user.getEmail(), "Mail token", mailLink+"/auth/verify?token="+encoder);
    }

    @Transactional 
    public void verifyMailToken(String token)throws NoSuchAlgorithmException{
       MailToken mt =  mailTokenRepository.findByTokenHash(hashToken(token)).orElseThrow(()-> new InvalidRefreshTokenException("Lien non valide !"));
       if(mt.isRevoked()){
        throw new InvalidRefreshTokenException("ce lien a déja été utilisé !");
       }
       if(mt.getExpiryDate().isBefore(LocalDateTime.now())){
        throw new InvalidRefreshTokenException("ce lien a éxpiré !");
       }

        mt.getUser().setEnabled(true);
        mt.setRevoked(true);
    }

    @Scheduled(fixedRate = 900000)
    public void nettoyerRefreshRepo(){
        mailTokenRepository.deleteRevokedAndExpired();
    }
    
}
