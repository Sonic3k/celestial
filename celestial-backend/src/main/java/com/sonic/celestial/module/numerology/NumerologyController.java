package com.sonic.celestial.module.numerology;

import com.sonic.celestial.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/numerology")
public class NumerologyController {

    private final NumerologyService service;

    public NumerologyController(NumerologyService service) {
        this.service = service;
    }

    @PostMapping("/calculate")
    public ApiResponse<NumerologyResult> calculate(@Valid @RequestBody NumerologyRequest request) {
        NumerologyResult result = service.calculate(request);
        return ApiResponse.ok(result);
    }
}