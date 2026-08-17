package com.sbgs.backend_2026.service.Competance;

import com.sbgs.backend_2026.entity.Competence;
import com.sbgs.backend_2026.entity.DocumentStage;
import com.sbgs.backend_2026.entity.Stage;
import com.sbgs.backend_2026.entity.StagiaireCompetence;
import com.sbgs.backend_2026.entity.enums.SourceCompetence;
import com.sbgs.backend_2026.entity.enums.TypeDocument;
import com.sbgs.backend_2026.repository.CompetenceRepository;
import com.sbgs.backend_2026.repository.DocumentStageRepository;
import com.sbgs.backend_2026.repository.StagiaireCompetenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CvExtractionService {

    private final DocumentStageRepository documentStageRepository;
    private final StagiaireCompetenceRepository stagiaireCompetenceRepository;
    private final CompetenceRepository competenceRepository;

    /*
     * Mots-clés verifies EN PLUS du referentiel officiel -- capture des
     * competences mentionnees dans un CV mais pas (encore) dans la table
     * "competence". Stockees quand meme (competence_id = null) : si le
     * referentiel est enrichi plus tard, elles redeviennent exploitables
     * sans devoir re-extraire le CV. Liste volontairement modeste.
     */
    private static final List<String> MOTS_CLES_SUPPLEMENTAIRES = List.of(
            // Techniques
            "Node.js", "React", "Vue.js", "TypeScript", "PHP", "C++", "C#",
            "Kotlin", "Swift", "Flutter", "Dart", "Symfony", "Django",
            "Redis", "Elasticsearch", "GraphQL", "Kafka", "RabbitMQ",
            "Terraform", "Ansible", "AWS", "Azure", "GCP",

            // Comportementales -- vocabulaire courant en CV, francais
            "Scrum", "Kanban", "Leadership", "Autonomie", "Rigueur",
            "Communication", "Adaptabilite", "Creativite", "Jira",
            "Esprit d'equipe", "Esprit d'analyse", "Esprit critique",
            "Gestion de temps", "Gestion de projet", "Force de proposition",
            "Sens de l'organisation", "Capacite d'adaptation",
            "Resolution de problemes", "Prise d'initiative", "Polyvalence",
            "Ecoute active", "Curiosite", "Perseverance", "Proactivite"
    );

    @Transactional
    public void extraireEtSynchroniser(Stage stage) {
        var demande = stage.getDemandeStage();
        var stagiaire = demande.getStagiaire();

        DocumentStage cv = documentStageRepository
                .findByDemandeStageIdAndTypeDocument(demande.getId(), TypeDocument.CV)
                .orElse(null);

        if (cv == null || cv.getFichier() == null) {
            log.warn("Aucun CV trouve pour la demande {} -- extraction ignoree", demande.getId());
            return;
        }

        String texte;
        try (PDDocument document = PDDocument.load(cv.getFichier())) {
            texte = new PDFTextStripper().getText(document).toLowerCase(Locale.FRENCH);
        } catch (IOException e) {
            log.warn("Impossible de lire le CV de la demande {} -- extraction ignoree", demande.getId(), e);
            return;
        }

        stagiaireCompetenceRepository.deleteByStagiaireIdAndSource(stagiaire.getId(), SourceCompetence.OCR_CV);

        Set<String> termesAVerifier = new LinkedHashSet<>();
        competenceRepository.findAll().forEach(c -> termesAVerifier.add(c.getNom()));
        termesAVerifier.addAll(MOTS_CLES_SUPPLEMENTAIRES);

        int lies = 0;
        int orphelins = 0;
        for (String terme : termesAVerifier) {
            if (texte.contains(terme.toLowerCase(Locale.FRENCH))) {
                Competence competenceExistante = competenceRepository.findByNomIgnoreCase(terme).orElse(null);

                StagiaireCompetence sc = StagiaireCompetence.builder()
                        .stagiaire(stagiaire)
                        .competence(competenceExistante) // peut etre null -- c'est voulu
                        .nomDetecte(terme)
                        .niveau(null)
                        .source(SourceCompetence.OCR_CV)
                        .build();
                stagiaireCompetenceRepository.save(sc);

                if (competenceExistante != null) lies++; else orphelins++;
            }
        }

        log.info("Extraction CV terminee pour le stagiaire {} -- {} liee(s) au referentiel, {} orpheline(s)",
                stagiaire.getId(), lies, orphelins);
    }
}