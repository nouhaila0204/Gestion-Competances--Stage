package com.sbgs.backend_2026.entity;

import com.sbgs.backend_2026.entity.enums.CategorieCompetence;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Référentiel unique de compétences, partagé entre les offres d'emploi,
 * les employés, les stagiaires et les critères d'évaluation. C'est ce
 * référentiel commun qui rend le matching possible : on compare des
 * competence_id, jamais du texte libre.
 */
@Entity
@Table(name = "competence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Competence {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36, updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    @Column(nullable = false, unique = true)
    @ToString.Include
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ToString.Include
    private CategorieCompetence categorie;

    @OneToMany(mappedBy = "competence")
    @Builder.Default
    private List<OffreCompetence> offresCompetence = new ArrayList<>();

    @OneToMany(mappedBy = "competence")
    @Builder.Default
    private List<EmployeCompetence> employeCompetences = new ArrayList<>();

    @OneToMany(mappedBy = "competence")
    @Builder.Default
    private List<StagiaireCompetence> stagiaireCompetences = new ArrayList<>();

    @OneToMany(mappedBy = "competence")
    @Builder.Default
    private List<CritereEvaluation> criteresEvaluation = new ArrayList<>();
}
