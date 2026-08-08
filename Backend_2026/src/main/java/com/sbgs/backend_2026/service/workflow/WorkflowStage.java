package com.sbgs.backend_2026.service.workflow;

import com.sbgs.backend_2026.entity.enums.EtatDossier;

public final class WorkflowStage {

    private WorkflowStage() {
    }

    public static void validerTransitionDossier(EtatDossier etatActuel) {
        if (etatActuel != EtatDossier.NOUVELLE) {
            throw new IllegalStateException(
                    "Un dossier ne peut etre traite qu'une seule fois, depuis l'etat NOUVELLE (etat actuel : "
                            + etatActuel + ")");
        }
    }

    public static void validerTransitionDecisionRh(EtatDossier etatDossier) {
        if (etatDossier != EtatDossier.ACCEPTEE) {
            throw new IllegalStateException(
                    "Le directeur RH ne peut decider que sur un dossier deja accepte par le responsable de stage (etat actuel : "
                            + etatDossier + ")");
        }
    }
}