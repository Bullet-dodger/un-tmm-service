package un.tmm.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialUpdateRequest {

    @Size(max = 50, message = "Formula must be at most 50 characters")
    private String formula;

    private String displayName;
}
