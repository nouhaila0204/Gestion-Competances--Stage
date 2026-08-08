package com.sbgs.backend_2026.entity;

import com.sbgs.backend_2026.entity.enums.NiveauCompetence;
import com.sbgs.backend_2026.entity.enums.SourceCompetence;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "stagiaire_competence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class StagiaireCompetence {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36, updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stagiaire_id", nullable = false)
    private Stagiaire stagiaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competence_id", nullable = false)
    private Competence competence;

    // Nullable : une competence extraite automatiquement du CV
    // (source = OCR_CV) n'a souvent pas de niveau associe.
    @Enumerated(EnumType.STRING)
    @ToString.Include
    private NiveauCompetence niveau;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ToString.Include
    private SourceCompetence source;
}
