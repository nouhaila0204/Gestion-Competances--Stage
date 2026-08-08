package com.sbgs.backend_2026.listener;

import com.sbgs.backend_2026.event.DecisionRhRefuseeEvent;
import com.sbgs.backend_2026.event.DossierAccepteEvent;
import com.sbgs.backend_2026.event.DossierRejeteEvent;
import com.sbgs.backend_2026.event.NewDemandeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DemandeStageNotificationListener {

    private final JavaMailSender mailSender;

    @Async
    @EventListener
    public void onNouvelleDemande(NewDemandeEvent event) {
        for (String email : event.emailsResponsablesStage()) {
            envoyerEmail(email, "Nouvelle demande de stage à traiter",
                    "Bonjour,\n\nUne nouvelle demande de stage (" + event.typeStage() + ") a été soumise par "
                            + event.stagiaireNomComplet() + ". Merci de vérifier le dossier sur la plateforme.");
        }
    }

    @Async
    @EventListener
    public void onDossierAccepte(DossierAccepteEvent event) {
        envoyerEmail(
                event.emailStagiaire(),
                "Votre dossier de stage a été accepté",
                "Bonjour " + event.prenomStagiaire() + ",\n\n" +
                        "Votre dossier de candidature au stage a été validé par le responsable de stage. " +
                        "Il est maintenant transmis au directeur RH pour approbation finale."
        );

        for (String email : event.emailsDirecteurRh()) {
            envoyerEmail(email, "Dossier de stage à valider",
                    "Bonjour,\n\nUn dossier de stage a été accepté par le responsable de stage et attend "
                            + "votre décision finale sur la plateforme.");
        }
    }

    @Async
    @EventListener
    public void onDossierRejete(DossierRejeteEvent event) {
        envoyerEmail(
                event.emailStagiaire(),
                "Votre dossier de stage a été rejeté",
                "Bonjour " + event.prenomStagiaire() + ",\n\n" +
                        "Votre dossier de candidature au stage a été rejeté par le responsable de stage, " +
                        "probablement en raison d'une pièce manquante ou pas bien visible." + ",\n" + "Contactez le service pour plus de détails."
        );
    }

    @Async
    @EventListener
    public void onDecisionRhRefusee(DecisionRhRefuseeEvent event) {
        envoyerEmail(
                event.emailStagiaire(),
                "Votre demande de stage n'a pas été retenue",
                "Bonjour " + event.prenomStagiaire() + ",\n\n" +
                        "Après examen, votre demande de stage n'a malheureusement pas été retenue" + ",\n" + "Bonne chance pour la prochaine fois."
        );
    }

    private void envoyerEmail(String destinataire, String sujet, String corps) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(destinataire);
            message.setSubject(sujet);
            message.setText(corps);
            mailSender.send(message);
            log.info("Email envoye a {} -- sujet : {}", destinataire, sujet);
        } catch (Exception e) {
            log.error("Echec de l'envoi d'email a {}", destinataire, e);
        }
    }
}