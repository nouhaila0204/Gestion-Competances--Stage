package com.sbgs.backend_2026.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        // 1. AUTORISATION POUR ACTUATOR (MONITORING)
                        .requestMatchers("/actuator/**").permitAll()

                        .requestMatchers("/api/auth/**", "/api/ecoles", "/api/candidatures").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/demandes").hasRole("STAGIAIRE")

                        .requestMatchers(HttpMethod.GET, "/api/demandes/*/documents").hasRole("RESPONSABLE_STAGE")
                        .requestMatchers(HttpMethod.GET, "/api/demandes").hasAnyRole( "STAGIAIRE","RESPONSABLE_STAGE", "DIRECTEUR_RH")
                        .requestMatchers(HttpMethod.GET, "/api/demandes/*").hasAnyRole("RESPONSABLE_STAGE", "STAGIAIRE")
                        .requestMatchers(HttpMethod.GET, "/api/documents/*/fichier").hasRole("RESPONSABLE_STAGE")

                        .requestMatchers(HttpMethod.PATCH, "/api/demandes/*/dossier").hasRole("RESPONSABLE_STAGE")
                        .requestMatchers(HttpMethod.PATCH, "/api/demandes/*/decision").hasRole("DIRECTEUR_RH")

                        .requestMatchers(HttpMethod.POST, "/api/stages/*/affectations").hasRole("DIRECTEUR_RH")

                        .requestMatchers(HttpMethod.POST, "/api/stages/*/absences").hasRole("RESPONSABLE_SERVICE")
                        .requestMatchers(HttpMethod.PATCH, "/api/stages/*/absences/**").hasRole("RESPONSABLE_SERVICE")
                        .requestMatchers(HttpMethod.GET, "/api/stages/*/absences")
                        .hasAnyRole("RESPONSABLE_SERVICE", "RESPONSABLE_STAGE")

                        .requestMatchers(HttpMethod.POST, "/api/stages/*/evaluations").hasRole("RESPONSABLE_SERVICE")
                        .requestMatchers(HttpMethod.PATCH, "/api/stages/*/evaluations/**").hasRole("RESPONSABLE_SERVICE")
                        .requestMatchers(HttpMethod.DELETE, "/api/stages/*/evaluations/**").hasRole("RESPONSABLE_SERVICE")
                        .requestMatchers(HttpMethod.GET, "/api/stages/*/evaluations").hasAnyRole("RESPONSABLE_SERVICE")

                        .requestMatchers(HttpMethod.GET, "/api/stages")
                        .hasAnyRole("RESPONSABLE_SERVICE","RESPONSABLE_STAGE")

                        .requestMatchers(HttpMethod.GET, "/api/stages/*")
                        .hasAnyRole("RESPONSABLE_SERVICE","RESPONSABLE_STAGE")

                        .requestMatchers(HttpMethod.GET, "/api/stages?responsableStageId").hasRole("RESPONSABLE_STAGE")


                        .requestMatchers(HttpMethod.POST, "/api/stages/*/rapport").hasRole("RESPONSABLE_SERVICE")
                        .requestMatchers(HttpMethod.GET, "/api/stages/*/rapport")
                        .hasAnyRole("RESPONSABLE_SERVICE", "RESPONSABLE_STAGE")

                        .requestMatchers(HttpMethod.POST, "/api/stages/*/valider").hasRole("RESPONSABLE_STAGE")
                        .requestMatchers(HttpMethod.PATCH, "/api/stages/*/attestation-remise").hasRole("RESPONSABLE_STAGE")

                        .requestMatchers(HttpMethod.POST, "/api/offres").hasRole("DIRECTEUR_RH")
                        .requestMatchers(HttpMethod.GET, "/api/offres/**").hasRole("DIRECTEUR_RH")
                        .requestMatchers(HttpMethod.GET, "/api/offres/*/matching").hasRole("DIRECTEUR_RH")

                        .requestMatchers(HttpMethod.GET, "/api/offres/***").hasRole("DIRECTEUR_RH")

                        .requestMatchers(HttpMethod.GET, "/api/stats/**").hasRole("RESPONSABLE_STAGE")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}