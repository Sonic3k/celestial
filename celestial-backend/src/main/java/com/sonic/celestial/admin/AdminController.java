package com.sonic.celestial.admin;

import com.sonic.celestial.common.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Admin endpoints — no auth (internal/admin tool only).
 *
 * GET  /api/admin/decks              → list all decks with seed status
 * POST /api/admin/seed               → seed a deck from Python script payload
 * POST /api/admin/jobs               → create a crawl job record
 * GET  /api/admin/jobs               → list recent 20 jobs
 * GET  /api/admin/jobs/{id}          → get single job (for polling)
 * PUT  /api/admin/jobs/{id}/log      → append log line (called by Python script)
 * DELETE /api/admin/decks/{id}       → delete deck + cards
 * PUT  /api/admin/decks/{id}/deactivate → soft-disable deck
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService svc;

    public AdminController(AdminService svc) { this.svc = svc; }

    // ── Decks ──────────────────────────────────────────────────

    @GetMapping("/decks")
    public ApiResponse<List<Map<String, Object>>> listDecks() {
        return ApiResponse.ok(svc.listAllDecks());
    }

    @DeleteMapping("/decks/{id}")
    public ApiResponse<Boolean> deleteDeck(@PathVariable Long id) {
        return ApiResponse.ok(svc.deleteDeck(id));
    }

    @PutMapping("/decks/{id}/deactivate")
    public ApiResponse<Boolean> deactivateDeck(@PathVariable Long id) {
        return ApiResponse.ok(svc.deactivateDeck(id));
    }

    // ── Seed ───────────────────────────────────────────────────

    @PostMapping("/seed")
    public ApiResponse<Map<String, Object>> seed(@RequestBody SeedRequest req) {
        Map<String, Object> result = svc.seed(req);
        boolean ok = (boolean) result.getOrDefault("success", false);
        if (!ok) return ApiResponse.fail((String) result.get("error"));
        return ApiResponse.ok(result);
    }

    // ── Jobs ───────────────────────────────────────────────────

    @PostMapping("/jobs")
    public ApiResponse<CrawlJob> createJob(@RequestBody Map<String, String> body) {
        String slug     = body.getOrDefault("deckSlug", "unknown");
        String provider = body.getOrDefault("provider", "manual");
        return ApiResponse.ok(svc.createJob(slug, provider));
    }

    @GetMapping("/jobs")
    public ApiResponse<List<CrawlJob>> listJobs() {
        return ApiResponse.ok(svc.recentJobs());
    }

    @GetMapping("/jobs/{id}")
    public ApiResponse<CrawlJob> getJob(@PathVariable Long id) {
        return svc.getJob(id)
                .map(ApiResponse::ok)
                .orElse(ApiResponse.fail("Job not found: " + id));
    }

    @PutMapping(value = "/jobs/{id}/log", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ApiResponse<Void> appendLog(@PathVariable Long id, @RequestBody String line) {
        svc.getJob(id).ifPresent(job -> {
            String existing = job.getLog() != null ? job.getLog() : "";
            job.setLog(existing + "\n" + line.strip());
            // We need a save — use the repo directly via service method
        });
        svc.appendJobLog(id, line.strip());
        return ApiResponse.ok(null);
    }
}
