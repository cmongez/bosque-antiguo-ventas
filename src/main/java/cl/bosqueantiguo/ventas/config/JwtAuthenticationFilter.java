package cl.bosqueantiguo.ventas.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    // IMPORTANTE: Esta clave debe ser la MISMA que usa el microservicio de usuarios
    private static final String SECRET_KEY = "mySecretKeySuperSeguraDeEjemplomySecretKeySuperSeguraDeEjemplomySecretKeySuperSeguraDeEjemplo";
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            try {
                Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
                
                String username = claims.getSubject();
                String userId = claims.get("userId", String.class);
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) claims.get("roles");
                
                if (username != null && roles != null) {
                    List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList());
                    
                    UsernamePasswordAuthenticationToken auth = 
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                    
                    // Agregamos userId como detalle para poder accederlo desde el controller
                    auth.setDetails(userId);
                    
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
                
            } catch (Exception e) {
                logger.error("Error validando JWT: " + e.getMessage());
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
