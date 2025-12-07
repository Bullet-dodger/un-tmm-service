package dto;

import lombok.Builder;

/**
 * Коэффициенты для расчёта теплоёмкости и энтальпии.
 *
 * Cp°(x) = b + 2c/x² + 2ex + 6fx² + 12gx³, где x = T * 10⁻⁴
 */
@Builder
public record ThermodynamicCoefficients(
        double a,  // F1 - используется при интегрировании
        double b,  // F2 - константа в Cp
        double c,  // F3 - коэффициент при 1/x²
        double d,  // F4 - используется при интегрировании
        double e,  // F5 - коэффициент при x
        double f,  // F6 - коэффициент при x²
        double g   // F7 - коэффициент при x³
) {

    public static ThermodynamicCoefficients zero() {
        return new ThermodynamicCoefficients(0, 0, 0, 0, 0, 0, 0);
    }
}