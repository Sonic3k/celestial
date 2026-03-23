package com.sonic.celestial.admin;

import com.sonic.celestial.module.tarot.Card;
import com.sonic.celestial.module.tarot.CardRepository;
import com.sonic.celestial.module.tarot.Deck;
import com.sonic.celestial.module.tarot.DeckRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AdminService {

    private final DeckRepository    deckRepo;
    private final CardRepository    cardRepo;
    private final CrawlJobRepository jobRepo;

    public AdminService(DeckRepository deckRepo, CardRepository cardRepo,
                        CrawlJobRepository jobRepo) {
        this.deckRepo = deckRepo;
        this.cardRepo = cardRepo;
        this.jobRepo  = jobRepo;
    }

    // ── Decks ──────────────────────────────────────────────────

    public List<Map<String, Object>> listAllDecks() {
        return deckRepo.findAll().stream().map(d -> Map.<String, Object>of(
                "id",            d.getId(),
                "module",        d.getModule(),
                "nameVi",        d.getNameVi(),
                "nameEn",        d.getNameEn(),
                "cardCount",     d.getCardCount(),
                "active",        d.isActive(),
                "seededCards",   cardRepo.countByDeckId(d.getId()),
                "style",         d.getStyle()         != null ? d.getStyle()         : "",
                "coverImageUrl", d.getCoverImageUrl() != null ? d.getCoverImageUrl() : ""
        )).toList();
    }

    @Transactional
    public boolean deactivateDeck(Long deckId) {
        return deckRepo.findById(deckId).map(deck -> {
            deck.setActive(false);
            deckRepo.save(deck);
            return true;
        }).orElse(false);
    }

    @Transactional
    public boolean deleteDeck(Long deckId) {
        if (!deckRepo.existsById(deckId)) return false;
        cardRepo.deleteAll(cardRepo.findByDeckId(deckId));
        deckRepo.deleteById(deckId);
        return true;
    }

    // ── Seed ───────────────────────────────────────────────────

    @Transactional
    public Map<String, Object> seed(SeedRequest req) {
        // Resolve or create deck
        Optional<Deck> existing = deckRepo.findAll().stream()
                .filter(d -> d.getNameEn().equalsIgnoreCase(req.deck.nameEn))
                .findFirst();

        Deck deck;
        if (existing.isPresent() && req.update) {
            deck = existing.get();
            cardRepo.deleteAll(cardRepo.findByDeckId(deck.getId()));
        } else if (existing.isPresent()) {
            return Map.of("success", false,
                    "error", "Deck '" + req.deck.nameEn + "' already exists. Pass update=true to replace.");
        } else {
            deck = new Deck();
        }

        deck.setModule("tarot");
        deck.setNameVi(req.deck.nameVi);
        deck.setNameEn(req.deck.nameEn);
        deck.setDescription(req.deck.description);
        deck.setCardCount(req.deck.cardCount > 0 ? req.deck.cardCount : req.cards.size());
        deck.setStyle(req.deck.style);
        deck.setCoverImageUrl(req.deck.coverImageUrl);
        deck.setBackImageUrl(req.deck.backImageUrl);
        deck.setActive(true);
        deck = deckRepo.save(deck);

        int count = 0;
        for (SeedRequest.CardPayload cp : req.cards) {
            Card c = new Card();
            c.setDeck(deck);
            c.setCardIndex(cp.cardIndex);
            c.setNameVi(cp.nameVi != null ? cp.nameVi : cp.nameEn);
            c.setNameEn(cp.nameEn);
            c.setArcana(cp.arcana);
            c.setSuit(cp.suit);
            c.setNumber(cp.number);
            c.setImageUrl(cp.imageUrl);
            c.setThumbnailUrl(cp.thumbnailUrl);
            c.setKeywordsUpright(cp.keywordsUpright);
            c.setKeywordsReversed(cp.keywordsReversed);
            c.setMeaningUpright(cp.meaningUpright);
            c.setMeaningReversed(cp.meaningReversed);
            c.setDescription(cp.description);
            c.setElement(cp.element);
            c.setPlanetOrSign(cp.planetOrSign);
            c.setNumerologyLink(cp.numerologyLink);
            cardRepo.save(c);
            count++;
        }

        // Update crawl job if linked
        if (req.jobId != null && !req.jobId.isBlank()) {
            try {
                long jid = Long.parseLong(req.jobId);
                jobRepo.findById(jid).ifPresent(job -> {
                    job.setStatus("DONE");
                    job.setSeededCards(count);
                    job.setFinishedAt(LocalDateTime.now());
                    appendLog(job, "[DONE] Seeded " + count + " cards → " + req.deck.nameEn);
                    jobRepo.save(job);
                });
            } catch (NumberFormatException ignored) {}
        }

        final int finalCount = count;
        final Long deckId    = deck.getId();
        return Map.of("success", true, "deckId", deckId,
                "deckName", deck.getNameEn(), "seededCards", finalCount);
    }

    // ── Jobs ───────────────────────────────────────────────────

    public CrawlJob createJob(String deckSlug, String provider) {
        CrawlJob job = new CrawlJob();
        job.setDeckSlug(deckSlug);
        job.setProvider(provider);
        job.setStatus("PENDING");
        job.setLog("[CREATED] Job queued: deck=" + deckSlug + " provider=" + provider);
        return jobRepo.save(job);
    }

    public List<CrawlJob> recentJobs() {
        return jobRepo.findTop20ByOrderByCreatedAtDesc();
    }

    public Optional<CrawlJob> getJob(Long id) {
        return jobRepo.findById(id);
    }

    @Transactional
    public void appendJobLog(Long id, String line) {
        jobRepo.findById(id).ifPresent(job -> {
            appendLog(job, line);
            jobRepo.save(job);
        });
    }

    // ── Private helpers ────────────────────────────────────────

    private void appendLog(CrawlJob job, String line) {
        String existing = job.getLog() != null ? job.getLog() : "";
        job.setLog(existing + "\n" + line);
    }
}
