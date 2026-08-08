package com.sbgs.backend_2026.service.workflow;

import com.sbgs.backend_2026.dto.Stage.PieceJointe;
import com.sbgs.backend_2026.entity.enums.TypeDocument;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class DossierChecklist {

    private static final Map<TypeDocument, Integer> QUANTITES_REQUISES = new EnumMap<>(TypeDocument.class);

    static {
        QUANTITES_REQUISES.put(TypeDocument.PHOTO, 1);
        QUANTITES_REQUISES.put(TypeDocument.ATTESTATION_ASSURANCE, 1);
        QUANTITES_REQUISES.put(TypeDocument.CONVENTION_STAGE, 1);
        QUANTITES_REQUISES.put(TypeDocument.DEMANDE_MANUSCRITE, 1);
        QUANTITES_REQUISES.put(TypeDocument.CNI, 1);
        QUANTITES_REQUISES.put(TypeDocument.CV, 1);
    }

    private DossierChecklist() {
    }

    public static Map<TypeDocument, Integer> piecesManquantes(List<PieceJointe> piecesFournies) {
        Map<TypeDocument, Long> comptesPresentes = piecesFournies.stream()
                .filter(PieceJointe::estFournie)
                .collect(Collectors.groupingBy(PieceJointe::type, Collectors.counting()));

        Map<TypeDocument, Integer> manquantes = new EnumMap<>(TypeDocument.class);
        QUANTITES_REQUISES.forEach((type, quantiteRequise) -> {
            long presentes = comptesPresentes.getOrDefault(type, 0L);
            if (presentes < quantiteRequise) {
                manquantes.put(type, (int) (quantiteRequise - presentes));
            }
        });
        return manquantes;
    }
}