package com.sbgs.backend_2026.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "stage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Stage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36, updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demande_id", nullable = false, unique = true)
    private DemandeStage demandeStage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_stage_id", nullable = false)
    private Personnel responsableStage;

    @Column(name = "date_debut")
    @ToString.Include
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    @ToString.Include
    private LocalDate dateFin;

    @Column(name = "date_generation_attestation")
    @ToString.Include
    private LocalDate dateGenerationAttestation;

    @Column(name = "attestation_remise", nullable = false)
    @Builder.Default
    @ToString.Include
    private boolean attestationRemise = false;

    @Lob
    @Column(name = "rapport_fichier", columnDefinition = "LONGBLOB")
    private byte[] rapportFichier;

    @Column(name = "rapport_nom_fichier")
    @ToString.Include
    private String rapportNomFichier;

    @Column(name = "rapport_type_contenu")
    @ToString.Include
    private String rapportTypeContenu;

    @Column(name = "rapport_date_depot")
    @ToString.Include
    private LocalDate rapportDateDepot;

    @Column(name = "theme_stage")
    @ToString.Include
    private String themeStage;

    @OneToMany(mappedBy = "stage", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Affectation> affectations = new ArrayList<>();

    @OneToMany(mappedBy = "stage", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Absence> absences = new ArrayList<>();

    @OneToMany(mappedBy = "stage", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Evaluation> evaluations = new ArrayList<>();

    @Lob
    @Column(name = "attestation_fichier", columnDefinition = "LONGBLOB")
    private byte[] attestationFichier;

    @Column(name = "attestation_nom_fichier")
    @ToString.Include
    private String attestationNomFichier;

    @Column(name = "attestation_type_contenu")
    @ToString.Include
    private String attestationTypeContenu;
}