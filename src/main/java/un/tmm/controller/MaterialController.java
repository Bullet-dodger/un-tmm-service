package un.tmm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import un.tmm.dto.request.MaterialCreateRequest;
import un.tmm.dto.request.MaterialUpdateRequest;
import un.tmm.dto.response.MaterialSearchResponse;
import un.tmm.service.MaterialService;

@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    @GetMapping
    public Page<MaterialSearchResponse> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return materialService.findAll(pageable);
    }

    @GetMapping("/search")
    public Page<MaterialSearchResponse> search(
            @RequestParam String query,
            @PageableDefault(size = 20) Pageable pageable) {
        return materialService.search(query, pageable);
    }

    @GetMapping("/{id}")
    public MaterialSearchResponse getById(@PathVariable Long id) {
        return materialService.findById(id);
    }

    // --- Admin endpoints ---

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MaterialSearchResponse create(@Valid @RequestBody MaterialCreateRequest request) {
        return materialService.create(request);
    }

    @PutMapping("/{id}")
    public MaterialSearchResponse update(
            @PathVariable Long id,
            @Valid @RequestBody MaterialUpdateRequest request) {
        return materialService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        materialService.delete(id);
    }
}
