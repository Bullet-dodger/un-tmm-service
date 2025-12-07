package model.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import model.entity.Material;

import java.util.Optional;

public interface MaterialRepository extends JpaRepository<Material, Long> {

    Optional<Material> findByFormulaIgnoreCase(String formula);

    boolean existsByFormula(String formula);
}