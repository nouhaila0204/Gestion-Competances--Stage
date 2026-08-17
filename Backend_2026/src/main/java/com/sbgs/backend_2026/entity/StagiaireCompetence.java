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

    // Nullable desormais : une competence detectee dans le CV mais absente
    // du referentiel n'a rien a quoi se lier -- voir nomDetecte ci-dessous.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competence_id")
    private Competence competence;

    // Toujours rempli pour une ligne OCR_CV (liee ou non) -- permet de
    // comparer un nom meme quand "competence" est null.
    @Column(name = "nom_detecte")
    @ToString.Include
    private String nomDetecte;

    @Enumerated(EnumType.STRING)
    @ToString.Include
    private NiveauCompetence niveau;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ToString.Include
    private SourceCompetence source;
}