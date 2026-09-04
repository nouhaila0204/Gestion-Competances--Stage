package com.sbgs.backend_2026.service;

import com.sbgs.backend_2026.dto.Stage.DemandeStageCreateRequest;
import com.sbgs.backend_2026.dto.Stage.DemandeStageResponse;
import com.sbgs.backend_2026.dto.Stage.PieceJointe;
import com.sbgs.backend_2026.entity.Ecole;
import com.sbgs.backend_2026.entity.enums.Role;
import com.sbgs.backend_2026.entity.Stagiaire;
import com.sbgs.backend_2026.entity.Utilisateur;
import com.sbgs.backend_2026.repository.EcoleRepository;
import com.sbgs.backend_2026.repository.StagiaireRepository;
import com.sbgs.backend_2026.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CandidatureService {

    private final UtilisateurRepository utilisateurRepository;
    private final StagiaireRepository stagiaireRepository;
    private final EcoleRepository ecoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final DemandeStageService demandeStageService;

    @Transactional
    public DemandeStageResponse postuler(String nom, String prenom, String email, String motDePasse, String telephone,
                                         String cni, DemandeStageCreateRequest request,
                                         List<PieceJointe> pieces) throws IOException {

        // 1. Créer l'utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail(email);
        utilisateur.setPasswordHash(passwordEncoder.encode(motDePasse));
        utilisateur.setRole(Role.STAGIAIRE);
        utilisateur.setActif(true);
        utilisateur = utilisateurRepository.save(utilisateur);

        // 2. Créer le stagiaire
        Stagiaire stagiaire = new Stagiaire();
        stagiaire.setUtilisateur(utilisateur);
        stagiaire.setNom(nom);
        stagiaire.setPrenom(prenom);
        stagiaire.setTelephone(telephone);
        stagiaire.setCni(cni);
        // Ajouter école, etc.
        Ecole ecole = ecoleRepository.findById(request.ecoleId())
                .orElseThrow(() -> new IllegalArgumentException("École introuvable"));
        stagiaire.setEcole(ecole);
        // niveauEtude, filiere à ajouter si vous avez des champs
        stagiaire = stagiaireRepository.save(stagiaire);

        // 3. Soumettre la demande
        return demandeStageService.soumettreDemande(request, pieces, email);
    }
}