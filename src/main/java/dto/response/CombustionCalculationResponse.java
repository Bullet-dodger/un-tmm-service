package dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CombustionCalculationResponse {

    private BigDecimal adiabaticTemperature;
    private BigDecimal reactionEnthalpy;
}
