package JobTrack;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                .info(new Info() //Définit les métadonnées générales affichées en haut de la page
                        .title("JobTrack API")
                        .description("API REST of job tracking with JWT authentification")
                        .version("1.0"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth")) // on applique le schema à tous les endpoints de l'API
                .components(new Components() //définit un schéma de sécurité (bearerAuth), qui dit à Swagger cette API utilise l'authentification par token Bearer, au format JWT
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                            )
                );
    }
}