package com.sbgs.backend_2026.dto.Stage;


import com.sbgs.backend_2026.entity.enums.TypeDocument;
import org.springframework.web.multipart.MultipartFile;

public record PieceJointe(TypeDocument type, MultipartFile fichier) {
    public boolean estFournie() {
        return fichier != null && !fichier.isEmpty();
    }
}
