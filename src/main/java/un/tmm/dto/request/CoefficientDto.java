package un.tmm.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import un.tmm.model.entity.ThermodynamicCoefficient;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoefficientDto {

    @NotNull
    private ThermodynamicCoefficient.PhaseState state;

    @NotNull
    private BigDecimal tMin;

    @NotNull
    private BigDecimal tMax;

    private BigDecimal calculationEnthalpy;

    private BigDecimal a;
    private BigDecimal b;
    private BigDecimal c;
    private BigDecimal d;
    private BigDecimal e;
    private BigDecimal f;
    private BigDecimal g;
}
