package com.sbgs.backend_2026.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "stagiaire")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Stagiaire {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36, updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false, unique = true)
    private Utilisateur utilisateur;

    @Column(nullable = false)
    @ToString.Include
    private String nom;

    @Column(nullable = false)
    @ToString.Include
    private String prenom;

    @Column(nullable = false, unique = true)
    @ToString.Include
    private String cni;

    @ToString.Include
    private String telephone;

    @OneToMany(mappedBy = "stagiaire", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DemandeStage> demandesStage = new ArrayList<>();

    // Competences du stagiaire (extraites du CV et/ou issues des evaluations
    // de stage) -- utilisees par l'algorithme de matching avec les offres.
    @OneToMany(mappedBy = "stagiaire", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StagiaireCompetence> competences = new ArrayList<>();
}
