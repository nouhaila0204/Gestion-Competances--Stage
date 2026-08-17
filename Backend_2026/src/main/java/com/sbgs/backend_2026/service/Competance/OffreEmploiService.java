package com.sbgs.backend_2026.service.Competance;

import com.sbgs.backend_2026.dto.Competences.OffreEmploiCreateRequest;
import com.sbgs.backend_2026.dto.Competences.OffreEmploiResponse;
import com.sbgs.backend_2026.entity.Competence;
import com.sbgs.backend_2026.entity.OffreCompetence;
import com.sbgs.backend_2026.entity.OffreEmploi;
import com.sbgs.backend_2026.repository.CompetenceRepository;
import com.sbgs.backend_2026.repository.OffreEmploiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OffreEmploiService {

    private final OffreEmploiRepository offreEmploiRepository;
    private final CompetenceRepository competenceRepository;

    @Transactional
    public OffreEmploiResponse creer(OffreEmploiCreateRequest request) {
        OffreEmploi offre = OffreEmploi.builder()
                .titre(request.titre())
                .description(request.description())
                .datePublication(request.datePublication())
                .statut(request.statut())
                .build();

        List<OffreCompetence> competences = new ArrayList<>();
        for (var cr : request.competencesRequises()) {
            Competence competence = competenceRepository.findById(cr.competenceId())
                    .orElseThrow(() -> new IllegalArgumentException("Competence introuvable : " + cr.competenceId()));
            competences.add(OffreCompetence.builder()
                    .offreEmploi(offre)
                    .competence(competence)
                    .niveauRequis(cr.niveauRequis())
                    .build());
        }
        offre.setCompetencesRequises(competences);

        return toResponse(offreEmploiRepository.save(offre));
    }

    @Transactional(readOnly = true)
    public List<OffreEmploiResponse> lister() {
        return offreEmploiRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OffreEmploiResponse obtenir(UUID id) {
        return offreEmploiRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Offre introuvable : " + id));
    }

    private OffreEmploiResponse toResponse(OffreEmploi o) {
        return new OffreEmploiResponse(
                o.getId(), o.getTitre(), o.getDescription(), o.getDatePublication(), o.getStatut(),
                o.getCompetencesRequises().stream()
                        .map(oc -> oc.getCompetence().getNom() + " (" + oc.getNiveauRequis() + ")")
                        .collect(Collectors.toList())
        );
    }
}