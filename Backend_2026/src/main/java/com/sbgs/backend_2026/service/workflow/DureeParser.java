package com.sbgs.backend_2026.service.workflow;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Calcule une date de fin approximative a partir d'un texte libre de duree
 * (ex. "2 mois", "8 semaines"). Approche best-effort : si le texte ne suit
 * aucun format reconnu (ex. "environ 2 mois et demi"), retourne
 * Optional.empty() -- la date de fin devra alors etre saisie manuellement.
 */
public final class DureeParser {

    private static final Pattern PATTERN = Pattern.compile(
            "(\\d+)\\s*(jour|semaine|mois|an)s?", Pattern.CASE_INSENSITIVE);

    private DureeParser() {
    }

    public static Optional<LocalDate> calculerDateFin(LocalDate dateDebut, String dureeTexte) {
        if (dateDebut == null || dureeTexte == null) {
            return Optional.empty();
        }

        Matcher matcher = PATTERN.matcher(dureeTexte.toLowerCase(Locale.FRENCH));
        if (!matcher.find()) {
            return Optional.empty();
        }

        int quantite = Integer.parseInt(matcher.group(1));
        String unite = matcher.group(2);

        LocalDate dateFin = switch (unite) {
            case "jour" -> dateDebut.plusDays(quantite);
            case "semaine" -> dateDebut.plusWeeks(quantite);
            case "mois" -> dateDebut.plusMonths(quantite);
            case "an" -> dateDebut.plusYears(quantite);
            default -> null;
        };

        return Optional.ofNullable(dateFin);
    }
}