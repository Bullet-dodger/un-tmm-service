package dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CombustionCalculationRequest {

    private BigDecimal initialTemperature;
    private BigDecimal finalTemperature;
    private List<MaterialQuantityDto> reagents;
    private List<MaterialQuantityDto> products;
}
