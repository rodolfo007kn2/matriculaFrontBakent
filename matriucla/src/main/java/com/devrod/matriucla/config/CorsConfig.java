package com.devrod.matriucla.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración global de CORS.
 * Define los orígenes permitidos de forma centralizada.
 * Elimina la necesidad de @CrossOrigin en cada controller.
 *
 * En producción (Docker + Nginx), el proxy de Nginx evita que CORS se active,
 * ya que el navegador nunca llama directamente al backend.
 * Esta configuración actúa como capa de seguridad adicional.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                        "http://localhost:4200",   // Desarrollo local con ng serve
                        "http://localhost:4201"    // Puerto alternativo si el 4200 está ocupado
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
