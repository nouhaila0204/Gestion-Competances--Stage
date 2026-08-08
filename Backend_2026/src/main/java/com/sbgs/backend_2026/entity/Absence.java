package com.sbgs.backend_2026.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "absence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Absence {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36, updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stage_id", nullable = false)
    private Stage stage;

    // Le responsable de service qui a marqué l'absence/retard
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marque_par")
    private Personnel marquePar;

    @Column(nullable = false)
    @ToString.Include
    private LocalDate date;

    @ToString.Include
    private String motif;

    @Column(nullable = false)
    @Builder.Default
    @ToString.Include
    private boolean justifiee = false;

    @Column(nullable = false)
    @Builder.Default
    @ToString.Include
    private boolean retard = false;
}
