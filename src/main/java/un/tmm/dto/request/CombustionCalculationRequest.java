package un.tmm.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CombustionCalculationRequest {

    @NotEmpty(message = "Reagents list must not be empty")
    private List<@Valid MaterialQuantityDto> reagents;

    @NotEmpty(message = "Products list must not be empty")
    private List<@Valid MaterialQuantityDto> products;
}
