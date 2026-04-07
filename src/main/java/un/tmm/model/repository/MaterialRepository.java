package un.tmm.model.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import un.tmm.model.entity.Material;

import java.util.Optional;

public interface MaterialRepository extends JpaRepository<Material, Long> {

    Optional<Material> findByFormulaIgnoreCase(String formula);

    /**
     * Full-text search with priority ordering:
     *   0 — exact match (formula = query, case-insensitive)
     *   1 — prefix match (formula starts with query)
     *   2 — contains match (formula contains query anywhere)
     * Within each tier results are ordered by formula length then alphabetically,
     * so "Si" appears before "Si1", and "Si1" before "Si1O2" etc.
     */
    @Query(
        value = """
            SELECT * FROM materials
            WHERE formula ILIKE '%' || :query || '%'
               OR display_name ILIKE '%' || :query || '%'
            ORDER BY
              CASE
                WHEN LOWER(formula) = LOWER(:query)              THEN 0
                WHEN LOWER(formula) LIKE LOWER(:query) || '%'    THEN 1
                ELSE 2
              END,
              LENGTH(formula),
              formula
            """,
        countQuery = """
            SELECT COUNT(*) FROM materials
            WHERE formula ILIKE '%' || :query || '%'
               OR display_name ILIKE '%' || :query || '%'
            """,
        nativeQuery = true
    )
    Page<Material> searchPrioritized(@Param("query") String query, Pageable pageable);

    boolean existsByFormula(String formula);
}
