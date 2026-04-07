package un.tmm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import un.tmm.dto.request.MaterialCreateRequest;
import un.tmm.dto.request.MaterialUpdateRequest;
import un.tmm.dto.response.MaterialSearchResponse;
import un.tmm.model.entity.Material;
import un.tmm.model.entity.ThermodynamicCoefficient;
import un.tmm.model.repository.MaterialRepository;
import un.tmm.model.repository.ThermodynamicCoefficientRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final ThermodynamicCoefficientRepository coefficientRepository;

    @Transactional(readOnly = true)
    public Page<MaterialSearchResponse> findAll(Pageable pageable) {
        return materialRepository.findAll(pageable).map(this::toSearchResponse);
    }

    @Transactional(readOnly = true)
    public Page<MaterialSearchResponse> search(String query, Pageable pageable) {
        return materialRepository.searchPrioritized(query, pageable)
                .map(this::toSearchResponse);
    }

    @Transactional(readOnly = true)
    public MaterialSearchResponse findById(Long id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new MaterialNotFoundException(id));
        return toSearchResponse(material);
    }

    @Transactional
    public MaterialSearchResponse create(MaterialCreateRequest request) {
        if (materialRepository.existsByFormula(request.getFormula())) {
            throw new MaterialAlreadyExistsException(request.getFormula());
        }

        Material material = Material.builder()
                .formula(request.getFormula())
                .displayName(request.getDisplayName())
                .build();

        if (request.getCoefficients() != null) {
            for (var coefDto : request.getCoefficients()) {
                ThermodynamicCoefficient coef = ThermodynamicCoefficient.builder()
                        .material(material)
                        .state(coefDto.getState())
                        .tMin(coefDto.getTMin())
                        .tMax(coefDto.getTMax())
                        .calculationEnthalpy(coefDto.getCalculationEnthalpy())
                        .a(coefDto.getA())
                        .b(coefDto.getB())
                        .c(coefDto.getC())
                        .d(coefDto.getD())
                        .e(coefDto.getE())
                        .f(coefDto.getF())
                        .g(coefDto.getG())
                        .build();
                material.getCoefficients().add(coef);
            }
        }

        Material saved = materialRepository.save(material);
        return toSearchResponse(saved);
    }

    @Transactional
    public MaterialSearchResponse update(Long id, MaterialUpdateRequest request) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new MaterialNotFoundException(id));

        if (request.getFormula() != null) {
            material.setFormula(request.getFormula());
        }
        if (request.getDisplayName() != null) {
            material.setDisplayName(request.getDisplayName());
        }

        Material saved = materialRepository.save(material);
        return toSearchResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!materialRepository.existsById(id)) {
            throw new MaterialNotFoundException(id);
        }
        materialRepository.deleteById(id);
    }

    private MaterialSearchResponse toSearchResponse(Material material) {
        BigDecimal tMin = coefficientRepository.findMinTemperatureByMaterialId(material.getId());
        BigDecimal tMax = coefficientRepository.findMaxTemperatureByMaterialId(material.getId());

        return MaterialSearchResponse.builder()
                .id(material.getId())
                .formula(material.getFormula())
                .displayName(material.getDisplayName())
                .tMin(tMin)
                .tMax(tMax)
                .build();
    }

    public static class MaterialNotFoundException extends RuntimeException {
        public MaterialNotFoundException(Long id) {
            super("Material not found with id: " + id);
        }
    }

    public static class MaterialAlreadyExistsException extends RuntimeException {
        public MaterialAlreadyExistsException(String formula) {
            super("Material already exists with formula: " + formula);
        }
    }
}
