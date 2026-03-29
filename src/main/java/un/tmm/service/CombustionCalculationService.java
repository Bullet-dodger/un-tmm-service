package un.tmm.service;


import un.tmm.dto.ReactionComponent;
import un.tmm.dto.TemperatureInterval;
import un.tmm.dto.ThermodynamicCoefficients;
import un.tmm.dto.request.CombustionCalculationRequest;
import un.tmm.dto.request.MaterialQuantityDto;
import un.tmm.dto.response.CombustionCalculationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import un.tmm.model.entity.ThermodynamicCoefficient;
import un.tmm.model.repository.ThermodynamicCoefficientRepository;
import un.tmm.util.ThermodynamicCalculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CombustionCalculationService {

    private static final double INITIAL_TEMPERATURE = 298.15;
    private static final double JOULES_PER_KILOJOULE = 1000.0;
    private static final double NEWTON_TOLERANCE = 1e-2;
    private static final int NEWTON_MAX_ITERATIONS = 100;
    private static final int INTEGRATION_STEPS = 10000;

    private final ThermodynamicCoefficientRepository coefficientRepository;

    public CombustionCalculationResponse calculate(CombustionCalculationRequest request) {
        List<ReactionComponent> reagents = buildReactionComponents(request.getReagents());
        List<ReactionComponent> products = buildReactionComponents(request.getProducts());

        calculatePhaseTransitionEnthalpies(products);

        double initialEnthalpy = calculateInitialEnthalpy(reagents, products);
        double adiabaticTemperature = findAdiabaticTemperature(INITIAL_TEMPERATURE, initialEnthalpy, products);

        return CombustionCalculationResponse.builder()
                .adiabaticTemperature(BigDecimal.valueOf(adiabaticTemperature).setScale(2, RoundingMode.HALF_UP))
                .reactionEnthalpy(BigDecimal.valueOf(initialEnthalpy / JOULES_PER_KILOJOULE).setScale(2, RoundingMode.HALF_UP))
                .build();
    }

    private List<ReactionComponent> buildReactionComponents(List<MaterialQuantityDto> materials) {
        List<ReactionComponent> components = new ArrayList<>();

        for (MaterialQuantityDto material : materials) {
            List<ThermodynamicCoefficient> dbCoefficients = coefficientRepository
                    .findByMaterialId(material.getMaterialId());

            dbCoefficients.sort(Comparator.comparing(ThermodynamicCoefficient::getTMin));

            List<TemperatureInterval> intervals = new ArrayList<>();
            double formationEnthalpy = 0.0;

            for (int i = 0; i < dbCoefficients.size(); i++) {
                ThermodynamicCoefficient dbCoef = dbCoefficients.get(i);

                if (i == 0 && dbCoef.getCalculationEnthalpy() != null) {
                    formationEnthalpy = dbCoef.getCalculationEnthalpy().doubleValue() * JOULES_PER_KILOJOULE;
                }

                ThermodynamicCoefficients coefficients = ThermodynamicCoefficients.builder()
                        .a(toDouble(dbCoef.getA()))
                        .b(toDouble(dbCoef.getB()))
                        .c(toDouble(dbCoef.getC()))
                        .d(toDouble(dbCoef.getD()))
                        .e(toDouble(dbCoef.getE()))
                        .f(toDouble(dbCoef.getF()))
                        .g(toDouble(dbCoef.getG()))
                        .build();

                TemperatureInterval interval = TemperatureInterval.builder()
                        .tMin(dbCoef.getTMin().doubleValue())
                        .tMax(dbCoef.getTMax().doubleValue())
                        .coefficients(coefficients)
                        .phaseTransitionEnthalpy(0.0)
                        .build();

                intervals.add(interval);
            }

            ReactionComponent component = ReactionComponent.builder()
                    .moleCount(material.getMoleCount().doubleValue())
                    .intervals(intervals)
                    .formationEnthalpy(formationEnthalpy)
                    .build();

            components.add(component);
        }

        return components;
    }

    private void calculatePhaseTransitionEnthalpies(List<ReactionComponent> products) {
        for (ReactionComponent product : products) {
            List<TemperatureInterval> intervals = product.getIntervals();
            double formationEnthalpy = product.getFormationEnthalpy();
            double previousEnthalpy = 0.0;

            for (int i = 0; i < intervals.size(); i++) {
                TemperatureInterval interval = intervals.get(i);

                if (i == 0) {
                    interval.setPhaseTransitionEnthalpy(0.0);
                } else {
                    TemperatureInterval previousInterval = intervals.get(i - 1);
                    double transitionTemperature = interval.getTMin();

                    double preFaseEnthalpy = ThermodynamicCalculator.calculateEnthalpy(
                            previousInterval.getCoefficients(), formationEnthalpy, transitionTemperature);
                    double currFaseEnthalpy = ThermodynamicCalculator.calculateEnthalpy(
                            interval.getCoefficients(), formationEnthalpy, transitionTemperature);

                    double transitionEnthalpy = preFaseEnthalpy - currFaseEnthalpy + previousEnthalpy;
                    interval.setPhaseTransitionEnthalpy(transitionEnthalpy);
                    previousEnthalpy = transitionEnthalpy;
                }
            }
        }
    }

    private double calculateInitialEnthalpy(List<ReactionComponent> reagents, List<ReactionComponent> products) {
        double enthalpy = 0.0;

        for (ReactionComponent product : products) {
            TemperatureInterval interval = product.getIntervals().get(0);
            enthalpy += product.getMoleCount() * ThermodynamicCalculator.calculateEnthalpy(
                    interval.getCoefficients(), product.getFormationEnthalpy(), INITIAL_TEMPERATURE);
        }

        for (ReactionComponent reagent : reagents) {
            TemperatureInterval interval = reagent.getIntervals().get(0);
            enthalpy -= reagent.getMoleCount() * ThermodynamicCalculator.calculateEnthalpy(
                    interval.getCoefficients(), reagent.getFormationEnthalpy(), INITIAL_TEMPERATURE);
        }

        return enthalpy;
    }

    private double getHeatCapacityAtTemperature(ReactionComponent component, double temperature) {
        List<TemperatureInterval> intervals = component.getIntervals();

        for (int i = 0; i < intervals.size(); i++) {
            TemperatureInterval interval = intervals.get(i);
            boolean isLastInterval = (i == intervals.size() - 1);

            if (isLastInterval) {
                if (temperature >= interval.getTMin()) {
                    return component.getMoleCount() * ThermodynamicCalculator.calculateHeatCapacity(
                            interval.getCoefficients(), temperature);
                }
            } else {
                if (temperature >= interval.getTMin() && temperature < interval.getTMax()) {
                    return component.getMoleCount() * ThermodynamicCalculator.calculateHeatCapacity(
                            interval.getCoefficients(), temperature);
                }
            }
        }

        return 0.0;
    }

    private double sumPhaseTransitionEnthalpies(double temperature, List<ReactionComponent> products) {
        double sum = 0.0;

        for (ReactionComponent product : products) {
            for (TemperatureInterval interval : product.getIntervals()) {
                if (temperature >= interval.getTMin() && temperature < interval.getTMax()) {
                    sum += product.getMoleCount() * interval.getPhaseTransitionEnthalpy();
                }
            }
        }

        return sum;
    }

    private double totalHeatCapacity(double temperature, List<ReactionComponent> products) {
        double total = 0.0;

        for (ReactionComponent product : products) {
            total += getHeatCapacityAtTemperature(product, temperature);
        }

        return total;
    }

    private double integrateHeatCapacity(double fromTemperature, double toTemperature, List<ReactionComponent> products) {
        double step = (toTemperature - fromTemperature) / INTEGRATION_STEPS;
        double integral = 0.0;

        for (int i = 0; i < INTEGRATION_STEPS; i++) {
            double t1 = fromTemperature + i * step;
            double t2 = t1 + step;
            integral += (totalHeatCapacity(t1, products) + totalHeatCapacity(t2, products)) * step / 2.0;
        }

        return integral;
    }

    private double calculateFinalEnthalpy(double temperature, double initialEnthalpy, List<ReactionComponent> products) {
        double integral = integrateHeatCapacity(INITIAL_TEMPERATURE, temperature, products);
        return integral + sumPhaseTransitionEnthalpies(temperature, products) - initialEnthalpy;
    }

    private double findAdiabaticTemperature(double initialGuess, double initialEnthalpy, List<ReactionComponent> products) {
        double temperature = initialGuess;

        for (int i = 0; i < NEWTON_MAX_ITERATIONS; i++) {
            double f = calculateFinalEnthalpy(temperature, initialEnthalpy, products);
            double fPlus = calculateFinalEnthalpy(temperature + NEWTON_TOLERANCE, initialEnthalpy, products);
            double fMinus = calculateFinalEnthalpy(temperature - NEWTON_TOLERANCE, initialEnthalpy, products);

            double derivative = (fPlus - fMinus) / (2 * NEWTON_TOLERANCE);
            double deltaT = -f / derivative;

            temperature += deltaT;

            if (Math.abs(deltaT) < NEWTON_TOLERANCE) {
                break;
            }
        }

        return temperature;
    }

    private double toDouble(BigDecimal value) {
        return value != null ? value.doubleValue() : 0.0;
    }
}
