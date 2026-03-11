package com.sonic.celestial.module.oracle;

import com.sonic.celestial.common.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/oracle")
public class OracleController {

    private final OracleService service;

    public OracleController(OracleService service) {
        this.service = service;
    }

    @GetMapping("/decks")
    public ApiResponse<List<OracleDeckDto>> listDecks() {
        return ApiResponse.ok(service.listDecks());
    }

    @PostMapping("/draw")
    public ApiResponse<OracleDrawResult> draw(@RequestBody OracleDrawRequest req) {
        return ApiResponse.ok(service.draw(req));
    }
}