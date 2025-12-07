package model.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "thermodynamic_coefficients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThermodynamicCoefficient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PhaseState state;

    @Column(name = "t_min", nullable = false, precision = 10, scale = 2)
    private BigDecimal tMin;

    @Column(name = "t_max", nullable = false, precision = 10, scale = 2)
    private BigDecimal tMax;

    @Column(name = "calculation_enthalpy", precision = 15, scale = 6)
    private BigDecimal calculationEnthalpy;

    @Column(precision = 20, scale = 10)
    private BigDecimal a;

    @Column(precision = 20, scale = 10)
    private BigDecimal b;

    @Column(precision = 20, scale = 10)
    private BigDecimal c;

    @Column(precision = 20, scale = 10)
    private BigDecimal d;

    @Column(precision = 20, scale = 10)
    private BigDecimal e;

    @Column(precision = 20, scale = 10)
    private BigDecimal f;

    @Column(precision = 20, scale = 10)
    private BigDecimal g;


    public enum PhaseState {
        SOLID,
        LIQUID,
        GAS
    }
}