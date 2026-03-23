package com.sonic.celestial.admin;

import com.sonic.celestial.common.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Admin REST API — all actions triggered from the Admin UI in the browser.
 *
 * GET    /api/admin/decks             → list all decks with seed status
 * GET    /api/admin/decks/catalogue   → list available deck slugs to crawl
 * POST   /api/admin/crawl/start       → start async crawl for a deck
 * GET    /api/admin/jobs              → list recent 20 jobs
 * GET    /api/admin/jobs/{id}         → poll single job status
 * DELETE /api/admin/decks/{id}        → delete deck + all cards
 * PUT    /api/admin/decks/{id}/deactivate
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService  adminSvc;
    private final CrawlService  crawlSvc;
    private final CrawlJobRepository jobRepo;

    public AdminController(AdminService adminSvc, CrawlService crawlSvc,
                           CrawlJobRepository jobRepo) {
        this.adminSvc = adminSvc;
        this.crawlSvc = crawlSvc;
        this.jobRepo  = jobRepo;
    }

    // ── Decks ──────────────────────────────────────────────────

    @GetMapping("/decks")
    public ApiResponse<List<Map<String, Object>>> listDecks() {
        return ApiResponse.ok(adminSvc.listAllDecks());
    }

    /** Returns available deck slugs the user can crawl from the UI */
    @GetMapping("/decks/catalogue")
    public ApiResponse<List<Map<String, Object>>> catalogue() {
        List<Map<String, Object>> list = CrawlService.DECKS.values().stream()
                .map(d -> Map.<String, Object>of(
                        "slug",      d.slug(),
                        "nameEn",    d.nameEn(),
                        "nameVi",    d.nameVi(),
                        "cardCount", d.cardCount(),
                        "style",     d.style()
                )).toList();
        return ApiResponse.ok(list);
    }

    @DeleteMapping("/decks/{id}")
    public ApiResponse<Boolean> deleteDeck(@PathVariable Long id) {
        return ApiResponse.ok(adminSvc.deleteDeck(id));
    }

    @PutMapping("/decks/{id}/deactivate")
    public ApiResponse<Boolean> deactivateDeck(@PathVariable Long id) {
        return ApiResponse.ok(adminSvc.deactivateDeck(id));
    }

    // ── Crawl ──────────────────────────────────────────────────

    /**
     * Start an async crawl job.
     * Body: { "deckSlug": "rider-waite", "skipImages": false, "update": false }
     */
    @PostMapping("/crawl/start")
    public ApiResponse<CrawlJob> startCrawl(@RequestBody Map<String, Object> body) {
        String  slug       = (String)  body.getOrDefault("deckSlug",    "rider-waite");
        boolean skipImages = (Boolean) body.getOrDefault("skipImages",  false);
        boolean update     = (Boolean) body.getOrDefault("update",      false);

        if (!CrawlService.DECKS.containsKey(slug)) {
            return ApiResponse.fail("Unknown deck slug: " + slug);
        }

        // Create job record first, then kick off async
        CrawlJob job = adminSvc.createJob(slug, "backend-crawler");
        crawlSvc.startCrawl(job.getId(), slug, skipImages, update);
        return ApiResponse.ok(job);
    }

    // ── Jobs ───────────────────────────────────────────────────

    @GetMapping("/jobs")
    public ApiResponse<List<CrawlJob>> listJobs() {
        return ApiResponse.ok(adminSvc.recentJobs());
    }

    @GetMapping("/jobs/{id}")
    public ApiResponse<CrawlJob> getJob(@PathVariable Long id) {
        return adminSvc.getJob(id)
                .map(ApiResponse::ok)
                .orElse(ApiResponse.fail("Job not found: " + id));
    }
}
