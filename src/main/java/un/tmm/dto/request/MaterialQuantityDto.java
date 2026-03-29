package un.tmm.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialQuantityDto {

    @NotNull(message = "Material ID is required")
    private Long materialId;

    @NotNull(message = "Mole count is required")
    @Positive(message = "Mole count must be positive")
    private BigDecimal moleCount;
}
