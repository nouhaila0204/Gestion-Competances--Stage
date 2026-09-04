package com.sbgs.backend_2026.config;

import com.sbgs.backend_2026.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class MigrationMotsDePasseTest implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        var utilisateurs = utilisateurRepository.findAll();
        int migres = 0;
        for (var u : utilisateurs) {
            if ("temp".equals(u.getPasswordHash())) {
                u.setPasswordHash(passwordEncoder.encode("password123"));
                utilisateurRepository.save(u);
                migres++;
            }
        }
        if (migres > 0) {
            log.info("{} mot(s) de passe de test migre(s) -- mot de passe pour tous : password123", migres);
        }
    }
}