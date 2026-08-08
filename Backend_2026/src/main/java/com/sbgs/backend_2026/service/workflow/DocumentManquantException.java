package com.sbgs.backend_2026.service.workflow;

import com.sbgs.backend_2026.entity.enums.TypeDocument;

import java.util.Map;

public class DocumentManquantException extends RuntimeException {
    public DocumentManquantException(Map<TypeDocument, Integer> piecesManquantes) {
        super("Dossier incomplet, pieces manquantes : " + piecesManquantes);
    }
}