package com.sbgs.backend_2026.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ATTENTION nom de classe : "Service" entre ici en conflit potentiel avec
 * l'annotation Spring @org.springframework.stereotype.Service, que tu vas
 * utiliser partout dans ta couche service (StagiaireService, etc.).
 * Si les deux sont importés dans le même fichier, utilise le nom complet
 * (com.gestionstages.entity.Service) ou renomme cette entité en "Departement"
 * si tu préfères éviter toute ambiguïté dès le départ.
 */
@Entity
@Table(name = "service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Services {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36, updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    @Column(nullable = false)
    @ToString.Include
    private String nom;

    @Column(name = "capacite_max")
    @ToString.Include
    private Integer capaciteMax;

    @OneToMany(mappedBy = "service")
    @Builder.Default
    private List<Affectation> affectations = new ArrayList<>();

    @OneToMany(mappedBy = "service")
    @Builder.Default
    private List<Personnel> personnels = new ArrayList<>();
}
