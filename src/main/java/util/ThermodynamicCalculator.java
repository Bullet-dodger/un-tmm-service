package util;


import dto.ThermodynamicCoefficients;

public final class ThermodynamicCalculator {

    private static final double TEMPERATURE_SCALE = 1e-4;

    private ThermodynamicCalculator() {
    }

    /**
     * Расчёт энтальпии по полиному.
     * H(T) = T * (b - 2c/x² - d/x + ex + 2fx² + 3gx³) + H298
     *
     * @param coef              коэффициенты
     * @param formationEnthalpy энтальпия образования H298 (Дж/моль)
     * @param temperature       температура (K)
     * @return энтальпия со знаком минус (для расчёта баланса)
     */
    public static double calculateEnthalpy(ThermodynamicCoefficients coef,
                                           double formationEnthalpy,
                                           double temperature) {
        double x = temperature * TEMPERATURE_SCALE;
        double x2 = x * x;
        double x3 = x2 * x;

        double enthalpy = temperature * (
                coef.b()
                        - 2 * coef.c() / x2
                        - coef.d() / x
                        + coef.e() * x
                        + 2 * coef.f() * x2
                        + 3 * coef.g() * x3
        ) + formationEnthalpy;

        return -enthalpy;
    }

    /**
     * Расчёт теплоёмкости по полиному.
     * Cp°(x) = b + 2c/x² + 2ex + 6fx² + 12gx³
     *
     * @param coef        коэффициенты
     * @param temperature температура (K)
     * @return теплоёмкость (Дж/(моль·К))
     */
    public static double calculateHeatCapacity(ThermodynamicCoefficients coef,
                                               double temperature) {
        double x = temperature * TEMPERATURE_SCALE;
        double x2 = x * x;
        double x3 = x2 * x;

        return coef.g()
                + 2 * coef.c() / x2
                + 2 * coef.e() * x
                + 6 * coef.f() * x2
                + 12 * coef.g() * x3;
    }
}