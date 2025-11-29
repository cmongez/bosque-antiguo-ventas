package cl.bosqueantiguo.ventas.config;

// Importa las anotaciones de configuración de Spring
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// Importa la clase RestTemplate, utilizada para hacer peticiones HTTP entre microservicios
import org.springframework.web.client.RestTemplate;

// Indica que esta clase es una clase de configuración para Spring
@Configuration
public class RestTemplateConfig {

    // Declara un bean de tipo RestTemplate para que pueda ser inyectado donde se necesite
    @Bean
    public RestTemplate restTemplate() {
        // Devuelve una nueva instancia de RestTemplate, que sirve para consumir APIs REST
        return new RestTemplate();
    }
}
