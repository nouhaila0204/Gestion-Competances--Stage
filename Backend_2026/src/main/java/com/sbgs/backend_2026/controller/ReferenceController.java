package com.sbgs.backend_2026.controller;

import com.sbgs.backend_2026.dto.Reference.*;
import com.sbgs.backend_2026.entity.CritereEvaluation;
import com.sbgs.backend_2026.entity.Personnel;
import com.sbgs.backend_2026.entity.enums.Role;
import com.sbgs.backend_2026.repository.CompetenceRepository;
import com.sbgs.backend_2026.repository.EcoleRepository;
import com.sbgs.backend_2026.repository.PersonnelRepository;
import com.sbgs.backend_2026.repository.ServiceRepository;
import com.sbgs.backend_2026.repository.CritereEvaluationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Regroupe les listes de reference utilisees pour peupler les menus
 * deroulants du frontend (ecoles, services, personnel, competences).
 * Separe volontairement des controleurs metier : ce sont de simples
 * lectures, sans logique metier a proteger derriere une couche service.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReferenceController {

    private final EcoleRepository ecoleRepository;
    private final ServiceRepository serviceRepository;
    private final PersonnelRepository personnelRepository;
    private final CompetenceRepository competenceRepository;
    private final CritereEvaluationRepository critereEvaluationRepository;

    @GetMapping("/ecoles")
    public List<EcoleResponse> listerEcoles() {
        return ecoleRepository.findAll().stream()
                .map(e -> new EcoleResponse(e.getId(), e.getNom(), e.getVille()))
                .toList();
    }

    @GetMapping("/services")
    public List<ServiceResponse> listerServices() {
        return serviceRepository.findAll().stream()
                .map(s -> new ServiceResponse(s.getId(), s.getNom()))
                .toList();
    }


    @GetMapping("/competences")
    public List<CompetenceResponse> listerCompetences() {
        return competenceRepository.findAll().stream()
                .map(c -> new CompetenceResponse(c.getId(), c.getNom(), c.getCategorie().name()))
                .toList();
    }

    @GetMapping("/personnel")
    public List<PersonnelResponse> listerPersonnel(@RequestParam(required = false) Role role) {
        var personnels = role != null
                ? personnelRepository.findByUtilisateur_Role(role)
                : personnelRepository.findAll();
        return personnels.stream()
                .map(p -> new PersonnelResponse(
                        p.getId(),
                        p.getPrenom() + " " + p.getNom(),
                        p.getService() != null ? p.getService().getId() : null,   // ✅ serviceId
                        p.getService() != null ? p.getService().getNom() : null   // serviceNom
                ))
                .toList();
    }

    //  NOUVEAU ENDPOINT pour extraire les critères d'évaluation
    @GetMapping("/critere-evaluations")
    public List<CritereEvaluationResponse> listerCriteres() {
        return critereEvaluationRepository.findAll().stream()
                .map(ce -> new CritereEvaluationResponse(
                        ce.getId(),
                        ce.getNom(),
                        ce.getCompetence() != null ? ce.getCompetence().getId() : null,
                        ce.getCompetence() != null ? ce.getCompetence().getCategorie().name() : null  // ✅ AJOUTÉ
                ))
                .toList();
    }

    //Pour recuperer id du respo service connecte
    @GetMapping("/personnel/me")
    public PersonnelResponse getCurrentPersonnel(Authentication authentication) {
        String email = authentication.getName();
        Personnel personnel = personnelRepository.findByUtilisateur_Email(email)
                .orElseThrow(() -> new IllegalArgumentException("Aucun personnel trouvé pour l'email : " + email));
        return new PersonnelResponse(
                personnel.getId(),
                personnel.getPrenom() + " " + personnel.getNom(),
                personnel.getService() != null ? personnel.getService().getId() : null,
                personnel.getService() != null ? personnel.getService().getNom() : null
        );
    }

}