package model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import model.entity.ThermodynamicCoefficient;

import java.math.BigDecimal;
import java.util.List;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ThermodynamicCoefficientRepository extends JpaRepository<ThermodynamicCoefficient, Long> {

    List<ThermodynamicCoefficient> findByMaterialId(Long materialId);

    List<ThermodynamicCoefficient> findByMaterialFormula(String formula);

    @Query("SELECT MIN(tc.tMin) FROM ThermodynamicCoefficient tc WHERE tc.material.id = :materialId")
    BigDecimal findMinTemperatureByMaterialId(@Param("materialId") Long materialId);

    @Query("SELECT MAX(tc.tMax) FROM ThermodynamicCoefficient tc WHERE tc.material.id = :materialId")
    BigDecimal findMaxTemperatureByMaterialId(@Param("materialId") Long materialId);
}