package com.sonic.celestial.module.tarot;

import com.sonic.celestial.common.ApiResponse;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tarot")
public class TarotController {

    private final TarotService service;

    public TarotController(TarotService service) {
        this.service = service;
    }

    @GetMapping("/decks")
    public ApiResponse<List<DeckDto>> listDecks() {
        return ApiResponse.ok(service.listDecks());
    }

    @PostMapping("/draw")
    public ApiResponse<TarotDrawResult> draw(@RequestBody TarotDrawRequest request) {
        return ApiResponse.ok(service.draw(request));
    }

    @GetMapping("/cards/{id}")
    public ApiResponse<CardDto> getCard(@PathVariable Long id) {
        return ApiResponse.ok(service.getCard(id));
    }
}