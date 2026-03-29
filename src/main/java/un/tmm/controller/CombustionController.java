package un.tmm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import un.tmm.dto.request.CombustionCalculationRequest;
import un.tmm.dto.response.CombustionCalculationResponse;
import un.tmm.service.CombustionCalculationService;

@RestController
@RequestMapping("/api/combustion")
@RequiredArgsConstructor
public class CombustionController {

    private final CombustionCalculationService calculationService;

    @PostMapping("/calculate")
    public CombustionCalculationResponse calculate(
            @Valid @RequestBody CombustionCalculationRequest request) {
        return calculationService.calculate(request);
    }
}
