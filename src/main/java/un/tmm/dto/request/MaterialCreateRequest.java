package un.tmm.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialCreateRequest {

    @NotBlank(message = "Formula is required")
    @Size(max = 50, message = "Formula must be at most 50 characters")
    private String formula;

    @NotBlank(message = "Display name is required")
    private String displayName;

    private List<@Valid CoefficientDto> coefficients;
}
