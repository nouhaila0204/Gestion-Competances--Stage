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
@Table(name = "offre_emploi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class OffreEmploi {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36, updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    @Column(nullable = false)
    @ToString.Include
    private String titre;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "date_publication")
    @ToString.Include
    private LocalDate datePublication;

    // Laisse en String volontairement : aucune liste de valeurs fixe n'a ete
    // definie pour ce champ dans le MCD. Transforme en enum (ex. OUVERTE /
    // FERMEE / POURVUE) si tu veux le contraindre plus tard.
    @ToString.Include
    private String statut;

    @OneToMany(mappedBy = "offreEmploi", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OffreCompetence> competencesRequises = new ArrayList<>();
}
