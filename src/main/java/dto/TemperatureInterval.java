package dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemperatureInterval {

    private double tMin;
    private double tMax;
    private ThermodynamicCoefficients coefficients;
    private double phaseTransitionEnthalpy;
}