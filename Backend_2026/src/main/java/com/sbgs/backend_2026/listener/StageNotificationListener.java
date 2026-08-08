package com.sbgs.backend_2026.listener;

import com.sbgs.backend_2026.event.StageApprouveEvent;
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
public class StageNotificationListener {

    private final JavaMailSender mailSender;

    @Async
    @EventListener
    public void onStageApprouve(StageApprouveEvent event) {
        envoyerEmail(event.emailStagiaire(), "Nouvelle affectation pour votre stage",
                "Bonjour " + event.prenomStagiaire() + ",\n\n" +
                        "Une affectation vient d'être confirmée pour votre stage : vous serez au service "
                        + event.nomService() + " du " + event.dateDebut() + " au " + event.dateFin() + ". " +
                        "Si votre stage se déroule dans plusieurs services (stage d'observation), vous recevrez " +
                        "un email similaire pour chaque service, au fur et à mesure des affectations." + "\n\n" +"Cordialement");

        for (String emailResponsable : event.emailsResponsablesService()) {
            envoyerEmail(emailResponsable, "Nouveau stagiaire affecté à votre service",
                    "Bonjour,\n\n" + event.nomCompletStagiaire() +
                            " va effectuer un stage dans votre service, du " +
                            event.dateDebut() + " au " + event.dateFin() + "." + "\n\n" +"Cordialement");
        }
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