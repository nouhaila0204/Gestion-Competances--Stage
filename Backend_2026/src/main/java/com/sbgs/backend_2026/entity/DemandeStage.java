package com.sbgs.backend_2026.entity;

import com.sbgs.backend_2026.entity.enums.DecisionRH;
import com.sbgs.backend_2026.entity.enums.EtatDossier;
import com.sbgs.backend_2026.entity.enums.TypeStage;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "demande_stage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class DemandeStage {

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
    @JoinColumn(name = "ecole_id", nullable = false)
    private Ecole ecole;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_stage", nullable = false)
    @ToString.Include
    private TypeStage typeStage;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat_dossier", nullable = false)
    @Builder.Default
    @ToString.Include
    private EtatDossier etatDossier = EtatDossier.NOUVELLE;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision_rh", nullable = false)
    @Builder.Default
    @ToString.Include
    private DecisionRH decisionRh = DecisionRH.REJETEE;

    @Column(name = "date_soumission", nullable = false)
    @Builder.Default
    @ToString.Include
    private LocalDate dateSoumission = LocalDate.now();

    @Column(name = "date_debut_souhaitee")
    @ToString.Include
    private LocalDate dateDebutSouhaitee;

    @Column(name = "duree_souhaitee")
    @ToString.Include
    private String dureeSouhaitee;

    @OneToMany(mappedBy = "demandeStage", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DocumentStage> documents = new ArrayList<>();

    // Cote proprietaire de la FK demande_id, sur la table STAGE -- voir Stage.demandeStage
    @OneToOne(mappedBy = "demandeStage", cascade = CascadeType.ALL, orphanRemoval = true)
    private Stage stage;
}
