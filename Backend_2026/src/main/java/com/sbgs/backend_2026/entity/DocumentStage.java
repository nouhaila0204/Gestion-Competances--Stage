package com.sbgs.backend_2026.entity;

import com.sbgs.backend_2026.entity.enums.TypeDocument;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "document_stage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class DocumentStage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36, updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demande_id", nullable = false)
    private DemandeStage demandeStage;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_document", nullable = false)
    @ToString.Include
    private TypeDocument typeDocument;

    @Lob
    @Column(name = "fichier", columnDefinition = "LONGBLOB")
    private byte[] fichier;

    @Column(name = "nom_fichier")
    @ToString.Include
    private String nomFichier; // nom original du fichier, ex: "cni_tazi.pdf"

    @Column(name = "type_contenu")
    @ToString.Include
    private String typeContenu; // MIME type, ex: "application/pdf"

    @Column(nullable = false)
    @Builder.Default
    @ToString.Include
    private boolean present = false;
}