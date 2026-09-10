package JobTrack;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Override
    protected void doFilterInternal(
        @NotNull HttpServletRequest request,
        @NotNull HttpServletResponse response,
        @NotNull FilterChain filterChain
    ) throws ServletException,IOException{

        String authHeader = request.getHeader("Authorization");
        if(authHeader == null ||!authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response); // on laisse passer au filtre suivant (pas de token donc pas d'authentification)
            return;
        }

        String token = authHeader.substring(7);

        String username = null;
        try{
            username = jwtService.extractUsername(token);
        }
        catch(Exception e){
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> JWT FILTER : " + request.getRequestURI());

            authHeader = request.getHeader("Authorization");

            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> AUTH HEADER : " + authHeader);
            // Si le token est invalide, on ne fait rien et on laisse passer au filtre suivant
        }
        

        if(username !=null && SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails userDetails = customUserDetailService.loadUserByUsername(username);

            if(jwtService.isTokenValid(token, username)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
