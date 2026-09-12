package JobTrack;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;


@Configuration
public class SecurityConfig {
    
    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired 
    private RateLimitingFilter rateLimitingFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean   //autoriser le CORS côté Spring Boot pour ne pas bloquer les requêtes venant du front 
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*")); // autoriser toutes les origines à accéder à l'API
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // autoriser toutes les méthodes HTTP
        configuration.setAllowedHeaders(List.of("*")); // autoriser tous les headers

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); 
        return source;
    }
    
    @Bean
    // comment verifier que le username et le mot de passe sont corrects
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(customUserDetailService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception{
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf(crsf->crsf.disable()) // on utilise des tokens donc pas besoin de CSRF (session,cookie)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth->auth
                .requestMatchers("/auth/**").permitAll() // on autorise l'accès à /auth/** sans authentification (requetes login/register)
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated() // toutes les autres requetes nécessitent une authentification (get/post/put/delete)
            )
            .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // on ne crée pas de session (on utilise des tokens)
            .authenticationProvider(authenticationProvider()) // on utilise notre provider d'authentification (qui vérifie le username et le mot de passe)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // on ajoute notre filtre d'authentification JWT avant le filtre d'authentification par défaut (qui vérifie le username et le mot de passe)
            //http.addFilterAfter(rateLimitingFilter, JwtAuthenticationFilter.class);
        return http.build();
    }

}
