package com.sonic.celestial.module.astrology;

import com.sonic.celestial.common.ApiResponse;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/astrology")
public class AstrologyController {

    private final AstrologyService service;

    public AstrologyController(AstrologyService service) {
        this.service = service;
    }

    @PostMapping("/calculate")
    public ApiResponse<AstrologyResult> calculate(@RequestBody AstrologyRequest request) {
        AstrologyResult result = service.calculate(request);
        return ApiResponse.ok(result);
    }
}