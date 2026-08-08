package com.sbgs.backend_2026.entity;

import com.sbgs.backend_2026.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/**
 * Table pivot d'authentification. Porte uniquement ce qui est commun
 * à tous les rôles (email, mot de passe, rôle). Les données métier
 * spécifiques vivent dans Stagiaire ou Personnel, reliées via utilisateur_id.
 */
@Entity
@Table(name = "utilisateur")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36, updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    @Column(nullable = false, unique = true)
    @ToString.Include
    private String email;

    // Volontairement absent du toString : ne jamais logger un hash de mot de passe.
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ToString.Include
    private Role role;

    @Column(nullable = false)
    @Builder.Default
    @ToString.Include
    private boolean actif = true;
}
