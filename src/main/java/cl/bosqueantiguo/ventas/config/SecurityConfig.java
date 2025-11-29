package cl.bosqueantiguo.ventas.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas
                .requestMatchers("/api/v1/public/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                // Crear venta: todos los usuarios autenticados (CLIENTE, VENDEDOR, ADMIN)
                .requestMatchers("POST", "/api/v1/sales").hasAnyRole("CLIENTE", "VENDEDOR", "ADMIN")
                // Listar todas las ventas: solo ADMIN y VENDEDOR
                .requestMatchers("GET", "/api/v1/sales").hasAnyRole("ADMIN", "VENDEDOR") 
                // Mis compras: todos los autenticados
                .requestMatchers("GET", "/api/v1/sales/my-orders").hasAnyRole("CLIENTE", "VENDEDOR", "ADMIN")
                // Ventas por usuario: ADMIN puede ver de cualquiera, otros solo las suyas
                .requestMatchers("GET", "/api/v1/sales/user/**").hasAnyRole("CLIENTE", "VENDEDOR", "ADMIN")
                // Venta específica: control en el controlador
                .requestMatchers("GET", "/api/v1/sales/**").hasAnyRole("CLIENTE", "VENDEDOR", "ADMIN")
                .anyRequest().authenticated()
            )
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(form -> form.disable())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // ORÍGENES PERMITIDOS (DEV + PROD)
        configuration.setAllowedOrigins(List.of(
            "http://localhost:5173",
            "https://mi-bucket-s3.amazonaws.com",
            "https://mi-dominio.com"
        ));
        
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
