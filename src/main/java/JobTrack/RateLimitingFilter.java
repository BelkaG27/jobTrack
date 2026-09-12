package JobTrack;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.time.Duration;

@Component
public class RateLimitingFilter extends OncePerRequestFilter{

    private ConcurrentHashMap<String,Bucket> bucketList = new ConcurrentHashMap<>();

    private Bucket createBucket(){
        return Bucket.builder()
            .addLimit(limit -> limit.capacity(20).refillGreedy(10, Duration.ofMinutes(1)))
            .build();
    }


    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For"); // on regarde le header "X-Forwarded-For" qui peut contenir :  192.168.1.25, 10.0.0.5, 10.0.0.10
        
        // Handle multiple IPs in X-Forwarded-For (comma-separated)
        if (ip != null && !ip.isEmpty()) {  
            ip = ip.split(",")[0].trim(); // donc on garde que le premier avec trim et [0]
        } else {
            // Fallback to the direct connection IP
            ip = request.getRemoteAddr(); // sinon on regarde l'IP de la connexion directe avec le serveur
        }
        
        return ip;
    }   

    @Override
    protected void doFilterInternal(
        @NotNull HttpServletRequest request,
        @NotNull HttpServletResponse response,
        @NotNull FilterChain filterChain
    ) throws ServletException,IOException{

        String cle;


        // quelle type de clé
        String URI = request.getRequestURI(); // on regarde la partie URI après le domaine et le port
        if(URI.startsWith("/auth/")){ // /auth/ siginifie qu'on est dans la route publique donc forcement l'utilisateur n'est pas authentifié
            cle = getClientIp(request); // on recupere son IP adresse 
        }else{ // sinon on recupere son username si il est authentifié, sinon on travaille tjrs avec l'ip adresse
            if(SecurityContextHolder.getContext().getAuthentication()!=null){
                cle = SecurityContextHolder.getContext().getAuthentication().getName();
            }else{
                cle = getClientIp(request);
            }
        }   

        

        Bucket bucket = bucketList.computeIfAbsent(cle, key -> createBucket());

        if(bucket.tryConsume(1)){
            filterChain.doFilter(request, response);
        }else{
            response.setStatus(429);
            response.getWriter().write("Too many requests");
        }


    }
    
}
