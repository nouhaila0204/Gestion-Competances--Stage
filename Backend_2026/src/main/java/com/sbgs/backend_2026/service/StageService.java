package com.sbgs.backend_2026.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.sbgs.backend_2026.dto.Stage.StageResponse;
import com.sbgs.backend_2026.entity.Stage;
import com.sbgs.backend_2026.repository.StageRepository;
import com.sbgs.backend_2026.service.Competance.CvExtractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.ColumnText;
import java.awt.Color;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StageService {

    private final StageRepository stageRepository;
    private final CvExtractionService cvExtractionService;

    @org.springframework.beans.factory.annotation.Value("${application.entreprise.nom:Nom de l'entreprise}")
    private String nomEntreprise;

    @org.springframework.beans.factory.annotation.Value("${application.entreprise.adresse:Adresse de l'entreprise}")
    private String adresseEntreprise;

    @org.springframework.beans.factory.annotation.Value("${application.entreprise.telephone:Telephone}")
    private String telephoneEntreprise;

    @Transactional(readOnly = true)
    public StageResponse obtenir(UUID id) {
        return toResponse(stageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Stage introuvable : " + id)));
    }

    @Transactional(readOnly = true)
    public List<StageResponse> lister(UUID responsableStageId) {
        if (responsableStageId != null) {
            return stageRepository.findMetadataByResponsableStageId(responsableStageId);
        }
        return stageRepository.findAllMetadata();
    }

    @Transactional
    public StageResponse deposerRapport(UUID id, String themeStage, MultipartFile fichier) throws IOException {
        Stage stage = stageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Stage introuvable : " + id));

        stage.setRapportFichier(fichier.getBytes());
        stage.setRapportNomFichier(fichier.getOriginalFilename());
        stage.setRapportTypeContenu(fichier.getContentType());
        stage.setRapportDateDepot(LocalDate.now());
        stage.setThemeStage(themeStage);

        return toResponse(stageRepository.save(stage));
    }

    @Transactional
    public StageResponse validerFinStage(UUID id) {
        Stage stage = stageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Stage introuvable : " + id));

        // Verification de "presence + rapport" simplifiee, vu le temps disponible :
        // le rapport doit exister. Le nombre d'absences reste consultable via
        // GET .../absences mais ne bloque pas encore la validation (pas de seuil
        // defini dans le cahier des charges) -- amelioration future possible.
        if (stage.getRapportFichier() == null) {
            throw new IllegalStateException("Le rapport de stage n'a pas encore ete depose");
        }
        if (stage.getDateGenerationAttestation() != null) {
            throw new IllegalStateException("L'attestation a deja ete generee pour ce stage");
        }

        cvExtractionService.extraireEtSynchroniser(stage);

        byte[] pdf = genererAttestationPdf(stage);
        stage.setAttestationFichier(pdf);
        stage.setAttestationNomFichier("attestation_stage_" + stage.getId() + ".pdf");
        stage.setAttestationTypeContenu("application/pdf");
        stage.setDateGenerationAttestation(LocalDate.now());

        return toResponse(stageRepository.save(stage));
    }

    @Transactional
    public StageResponse marquerAttestationRemise(UUID id) {
        Stage stage = stageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Stage introuvable : " + id));

        if (stage.getDateGenerationAttestation() == null) {
            throw new IllegalStateException("L'attestation n'a pas encore ete generee pour ce stage");
        }

        stage.setAttestationRemise(true);
        return toResponse(stageRepository.save(stage));
    }

    @Transactional(readOnly = true)
    public Stage obtenirEntite(UUID id) {
        return stageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Stage introuvable : " + id));
    }

    private byte[] genererAttestationPdf(Stage stage) {
        var demande = stage.getDemandeStage();
        var stagiaire = demande.getStagiaire();
        String services = stage.getAffectations().stream()
                .map(a -> a.getService().getNom())
                .distinct()
                .collect(Collectors.joining(", "));

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Marge du haut pour le logo, marge du bas pour la bande rouge (~3 cm + espace)
            Document document = new Document(PageSize.A4, 36, 36, 130, 100);
            PdfWriter writer = PdfWriter.getInstance(document, out);
            writer.setPageEvent(new EnTeteAttestation(nomEntreprise, adresseEntreprise, telephoneEntreprise));
            document.open();

            Font titreFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font texteFont = new Font(Font.HELVETICA, 12, Font.NORMAL);
            Font texteBoldFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font noteFont = new Font(Font.HELVETICA, 10, Font.ITALIC);
            Font signatureFont = new Font(Font.HELVETICA, 11, Font.NORMAL);

            Paragraph titre = new Paragraph("Attestation de stage", titreFont);
            titre.setAlignment(Element.ALIGN_CENTER);
            titre.setSpacingAfter(30);
            document.add(titre);

            document.add(new Paragraph("Nous, soussignés, " + nomEntreprise + ", attestons que :", texteFont));
            document.add(new Paragraph(" "));

            Paragraph nomLigne = new Paragraph();
            nomLigne.add(new Chunk("Nom et prénom : ", texteFont));
            nomLigne.add(new Chunk(stagiaire.getPrenom() + " " + stagiaire.getNom(), texteBoldFont));
            document.add(nomLigne);

            Paragraph cniLigne = new Paragraph();
            cniLigne.add(new Chunk("CNI : ", texteFont));
            cniLigne.add(new Chunk(stagiaire.getCni(), texteBoldFont));
            document.add(cniLigne);

            document.add(new Paragraph(" "));

            Paragraph typeLigne = new Paragraph();
            typeLigne.add(new Chunk("A effectué un stage de type ", texteFont));
            typeLigne.add(new Chunk(String.valueOf(demande.getTypeStage()), texteBoldFont));
            typeLigne.add(new Chunk(" au sein de notre entreprise,", texteFont));
            document.add(typeLigne);

            document.add(new Paragraph("du " + stage.getDateDebut() + " au " + stage.getDateFin() + ",", texteFont));

            Paragraph serviceLigne = new Paragraph();
            serviceLigne.add(new Chunk("dans le(s) service(s) suivant(s) : ", texteFont));
            serviceLigne.add(new Chunk(services, texteBoldFont));
            serviceLigne.add(new Chunk(".", texteFont));
            document.add(serviceLigne);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Fait pour servir et valoir ce que de droit.", texteFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Signé le : ______________________________", signatureFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Signé par : ______________________________", signatureFont));
            document.add(new Paragraph("               Directeur des Ressources Humaines", noteFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(
                    "Cette attestation doit être signée et cachetée par le directeur des ressources humaines pour être valide.",
                    noteFont));

            document.close();
            return out.toByteArray();
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("Erreur lors de la generation de l'attestation", e);
        }
    }

    private StageResponse toResponse(Stage s) {
        return new StageResponse(
                s.getId(), s.getDateDebut(), s.getDateFin(), s.getDateGenerationAttestation(),
                s.isAttestationRemise(), s.getRapportNomFichier(), s.getRapportTypeContenu(),
                s.getRapportDateDepot(), s.getThemeStage(),
                s.getResponsableStage() != null ? s.getResponsableStage().getId() : null
        );
    }

    private static class EnTeteAttestation extends PdfPageEventHelper {

        private final String nomEntreprise;
        private final String adresse;
        private final String telephone;

        EnTeteAttestation(String nomEntreprise, String adresse, String telephone) {
            this.nomEntreprise = nomEntreprise;
            this.adresse = adresse;
            this.telephone = telephone;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte canvas = writer.getDirectContent();
            float pageWidth = document.getPageSize().getWidth();
            float pageHeight = document.getPageSize().getHeight();

            // Logo en haut a gauche
            try {
                var logoUrl = getClass().getResource("/imgs/logo-SDGS.png");
                if (logoUrl != null) {
                    Image logo = Image.getInstance(logoUrl);
                    logo.scaleToFit(90, 60);
                    logo.setAbsolutePosition(36, pageHeight - 95);
                    canvas.addImage(logo);
                }
            } catch (Exception e) {
                // logo absent ou illisible -- on continue sans
            }

            // Bande rouge en bas de la PAGE ENTIERE (pas sous le logo), 3 cm de hauteur
            float bandeHauteur = 85f; // ~3 cm
            canvas.saveState();
            canvas.setColorFill(new Color(214, 40, 40));
            canvas.rectangle(0, 0, pageWidth, bandeHauteur);
            canvas.fill();
            canvas.restoreState();

            // Texte blanc centre dans la bande : nom, adresse, telephone
            Font nomFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);
            Font detailFont = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.WHITE);
            float centreX = pageWidth / 2;

            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                    new Phrase(nomEntreprise, nomFont), centreX, 58, 0);
            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                    new Phrase(adresse, detailFont), centreX, 42, 0);
            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                    new Phrase("Tél : " + telephone, detailFont), centreX, 28, 0);
        }
    }
}
