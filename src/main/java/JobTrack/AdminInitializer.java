package JobTrack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component 
public class AdminInitializer implements CommandLineRunner{
    
    @Autowired 
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Value("${admin.password}")
    private String passwordAdmin; 

    @Override 
    public void run(String... args){
        if(!userRepository.existsByRole(Role.ROLE_ADMIN)){
            User user = new User("bbk", passwordEncoder.encode(passwordAdmin), "bbk@gmail.com", "dev", 67, Role.ROLE_ADMIN);
            userRepository.save(user);
        }
    }
}
