package com.example.demo.SecurityConfig;

import com.example.demo.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Enable CORS (uses the CorsConfigurationSource bean below)
            .cors(cors -> {})
            // Disable CSRF for this example
            .csrf(csrf -> csrf.disable())
            // Configure authentication
            .userDetailsService(userDetailsService)
            // Configure authorization rules
            .authorizeHttpRequests(auth -> auth
                // Always allow preflight requests
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // Public authentication endpoints
                .requestMatchers("/auth/**").permitAll()
                
                // Public car browsing endpoints
                .requestMatchers(HttpMethod.GET, "/api/cars/available/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/cars/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/cars/search").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/cars/filter").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/cars/type/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/cars/filter-options").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/cars/{id}/availability").permitAll()
                
                // USER role endpoints (booking and user-specific actions)
                .requestMatchers(HttpMethod.POST, "/api/forms/book").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/cars/{id}/book").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/api/forms/user/**").hasRole("USER")
                
                // ADMIN/MANAGER role endpoints (car management)
                .requestMatchers(HttpMethod.POST, "/api/cars").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers(HttpMethod.PUT, "/api/cars/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/api/cars/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/cars/{id}/release").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers(HttpMethod.GET, "/api/cars/all").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers(HttpMethod.GET, "/api/cars/statistics").hasAnyRole("ADMIN", "MANAGER")
                
                // ADMIN only endpoints (user management, all forms)
                .requestMatchers(HttpMethod.GET, "/api/forms/recent").hasRole("ADMIN")
                
                // Require authentication for any other request
                .anyRequest().authenticated()
            )
            // Use HTTP Basic authentication
            .httpBasic(httpBasic -> {});

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}