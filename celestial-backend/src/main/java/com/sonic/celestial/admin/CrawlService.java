package com.sonic.celestial.admin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonic.celestial.module.tarot.Card;
import com.sonic.celestial.module.tarot.CardRepository;
import com.sonic.celestial.module.tarot.Deck;
import com.sonic.celestial.module.tarot.DeckRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Runs the full tarot crawl pipeline entirely within the backend — no local scripts needed.
 *
 * Pipeline per deck:
 *   1. Fetch card metadata from tarotapi.dev
 *   2. Enrich with github.com/ekelen/tarot-json
 *   3. Download images from Wikimedia Commons
 *   4. Upload images to Backblaze B2 → get BunnyCDN URLs
 *   5. Seed cards into PostgreSQL
 */
@Service
public class CrawlService {

    // ── Deck catalogue ─────────────────────────────────────────
    public static final Map<String, DeckConfig> DECKS = Map.of(
        "rider-waite", new DeckConfig(
            "rider-waite",
            "Rider-Waite-Smith", "Rider-Waite-Smith",
            "Bộ bài Tarot cổ điển nhất, vẽ bởi Pamela Colman Smith (1909). Nền tảng của hầu hết các bộ bài hiện đại.",
            78, "classic"
        )
    );

    public record DeckConfig(String slug, String nameEn, String nameVi,
                              String description, int cardCount, String style) {}

    // ── Wikimedia image map (card index → Commons filename) ────
    private static final Map<Integer, String> WIKIMEDIA = new HashMap<>();
    static {
        // Major Arcana
        WIKIMEDIA.put(0,  "RWS_Tarot_00_Fool.jpg");
        WIKIMEDIA.put(1,  "RWS_Tarot_01_Magician.jpg");
        WIKIMEDIA.put(2,  "RWS_Tarot_02_High_Priestess.jpg");
        WIKIMEDIA.put(3,  "RWS_Tarot_03_Empress.jpg");
        WIKIMEDIA.put(4,  "RWS_Tarot_04_Emperor.jpg");
        WIKIMEDIA.put(5,  "RWS_Tarot_05_Hierophant.jpg");
        WIKIMEDIA.put(6,  "RWS_Tarot_06_Lovers.jpg");
        WIKIMEDIA.put(7,  "RWS_Tarot_07_Chariot.jpg");
        WIKIMEDIA.put(8,  "RWS_Tarot_08_Strength.jpg");
        WIKIMEDIA.put(9,  "RWS_Tarot_09_Hermit.jpg");
        WIKIMEDIA.put(10, "RWS_Tarot_10_Wheel_of_Fortune.jpg");
        WIKIMEDIA.put(11, "RWS_Tarot_11_Justice.jpg");
        WIKIMEDIA.put(12, "RWS_Tarot_12_Hanged_Man.jpg");
        WIKIMEDIA.put(13, "RWS_Tarot_13_Death.jpg");
        WIKIMEDIA.put(14, "RWS_Tarot_14_Temperance.jpg");
        WIKIMEDIA.put(15, "RWS_Tarot_15_Devil.jpg");
        WIKIMEDIA.put(16, "RWS_Tarot_16_Tower.jpg");
        WIKIMEDIA.put(17, "RWS_Tarot_17_Star.jpg");
        WIKIMEDIA.put(18, "RWS_Tarot_18_Moon.jpg");
        WIKIMEDIA.put(19, "RWS_Tarot_19_Sun.jpg");
        WIKIMEDIA.put(20, "RWS_Tarot_20_Judgement.jpg");
        WIKIMEDIA.put(21, "RWS_Tarot_21_World.jpg");
        // Wands
        WIKIMEDIA.put(22,"Wands01.jpg"); WIKIMEDIA.put(23,"Wands02.jpg");
        WIKIMEDIA.put(24,"Wands03.jpg"); WIKIMEDIA.put(25,"Wands04.jpg");
        WIKIMEDIA.put(26,"Wands05.jpg"); WIKIMEDIA.put(27,"Wands06.jpg");
        WIKIMEDIA.put(28,"Wands07.jpg"); WIKIMEDIA.put(29,"Wands08.jpg");
        WIKIMEDIA.put(30,"Wands09.jpg"); WIKIMEDIA.put(31,"Wands10.jpg");
        WIKIMEDIA.put(32,"Wands11.jpg"); WIKIMEDIA.put(33,"Wands12.jpg");
        WIKIMEDIA.put(34,"Wands13.jpg"); WIKIMEDIA.put(35,"Wands14.jpg");
        // Cups
        WIKIMEDIA.put(36,"Cups01.jpg"); WIKIMEDIA.put(37,"Cups02.jpg");
        WIKIMEDIA.put(38,"Cups03.jpg"); WIKIMEDIA.put(39,"Cups04.jpg");
        WIKIMEDIA.put(40,"Cups05.jpg"); WIKIMEDIA.put(41,"Cups06.jpg");
        WIKIMEDIA.put(42,"Cups07.jpg"); WIKIMEDIA.put(43,"Cups08.jpg");
        WIKIMEDIA.put(44,"Cups09.jpg"); WIKIMEDIA.put(45,"Cups10.jpg");
        WIKIMEDIA.put(46,"Cups11.jpg"); WIKIMEDIA.put(47,"Cups12.jpg");
        WIKIMEDIA.put(48,"Cups13.jpg"); WIKIMEDIA.put(49,"Cups14.jpg");
        // Swords
        WIKIMEDIA.put(50,"Swords01.jpg"); WIKIMEDIA.put(51,"Swords02.jpg");
        WIKIMEDIA.put(52,"Swords03.jpg"); WIKIMEDIA.put(53,"Swords04.jpg");
        WIKIMEDIA.put(54,"Swords05.jpg"); WIKIMEDIA.put(55,"Swords06.jpg");
        WIKIMEDIA.put(56,"Swords07.jpg"); WIKIMEDIA.put(57,"Swords08.jpg");
        WIKIMEDIA.put(58,"Swords09.jpg"); WIKIMEDIA.put(59,"Swords10.jpg");
        WIKIMEDIA.put(60,"Swords11.jpg"); WIKIMEDIA.put(61,"Swords12.jpg");
        WIKIMEDIA.put(62,"Swords13.jpg"); WIKIMEDIA.put(63,"Swords14.jpg");
        // Pentacles
        WIKIMEDIA.put(64,"Pents01.jpg"); WIKIMEDIA.put(65,"Pents02.jpg");
        WIKIMEDIA.put(66,"Pents03.jpg"); WIKIMEDIA.put(67,"Pents04.jpg");
        WIKIMEDIA.put(68,"Pents05.jpg"); WIKIMEDIA.put(69,"Pents06.jpg");
        WIKIMEDIA.put(70,"Pents07.jpg"); WIKIMEDIA.put(71,"Pents08.jpg");
        WIKIMEDIA.put(72,"Pents09.jpg"); WIKIMEDIA.put(73,"Pents10.jpg");
        WIKIMEDIA.put(74,"Pents11.jpg"); WIKIMEDIA.put(75,"Pents12.jpg");
        WIKIMEDIA.put(76,"Pents13.jpg"); WIKIMEDIA.put(77,"Pents14.jpg");
    }

    private static final String[] MAJOR_VI = {
        "Kẻ Điên","Nhà Ảo Thuật","Nữ Tư Tế","Hoàng Hậu","Hoàng Đế",
        "Giáo Hoàng","Người Tình","Cỗ Xe","Sức Mạnh","Ẩn Sĩ",
        "Bánh Xe Số Phận","Công Lý","Người Treo Ngược","Thần Chết",
        "Sự Điều Độ","Ác Quỷ","Tòa Tháp","Ngôi Sao","Mặt Trăng",
        "Mặt Trời","Sự Phán Xét","Thế Giới"
    };
    private static final Map<String,String> SUIT_VI = Map.of(
        "wands","Gậy","cups","Chén","swords","Kiếm","pentacles","Đồng Tiền"
    );
    private static final Map<String,String> RANK_VI = Map.of(
        "Ace","Át","Two","Hai","Three","Ba","Four","Bốn","Five","Năm",
        "Six","Sáu","Seven","Bảy","Eight","Tám","Nine","Chín","Ten","Mười"
    );
    private static final Map<String,String> RANK_VI2 = Map.of(
        "Page","Thị Đồng","Knight","Kỵ Sĩ","Queen","Nữ Hoàng","King","Vua"
    );

    // ── Dependencies ────────────────────────────────────────────
    private final CrawlJobRepository jobRepo;
    private final DeckRepository     deckRepo;
    private final CardRepository     cardRepo;
    private final ImageStorageService imageStorage;
    private final ObjectMapper        mapper = new ObjectMapper();
    private final HttpClient          http   = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL).build();

    public CrawlService(CrawlJobRepository jobRepo, DeckRepository deckRepo,
                        CardRepository cardRepo, ImageStorageService imageStorage) {
        this.jobRepo       = jobRepo;
        this.deckRepo      = deckRepo;
        this.cardRepo      = cardRepo;
        this.imageStorage  = imageStorage;
    }

    // ── Start a crawl job (async) ───────────────────────────────

    @Async
    public void startCrawl(Long jobId, String deckSlug, boolean skipImages, boolean update) {
        CrawlJob job = jobRepo.findById(jobId).orElseThrow();
        job.setStatus("RUNNING");
        job.setStartedAt(LocalDateTime.now());
        jobRepo.save(job);

        try {
            DeckConfig cfg = DECKS.get(deckSlug);
            if (cfg == null) throw new IllegalArgumentException("Unknown deck slug: " + deckSlug);

            log(job, "[1/4] Fetching card data from tarotapi.dev...");
            List<Map<String, Object>> cards = fetchCards(job);
            log(job, "      Got " + cards.size() + " cards");

            log(job, "[2/4] Enriching from github.com/ekelen/tarot-json...");
            enrichCards(cards, job);

            if (!skipImages && imageStorage.isConfigured()) {
                log(job, "[3/4] Downloading & uploading images to B2...");
                uploadImages(cards, deckSlug, job);
            } else {
                log(job, "[3/4] Skipping images (B2 not configured or skipImages=true)");
            }

            log(job, "[4/4] Seeding into database...");
            int seeded = seedDeck(cfg, cards, update, job);

            job.setStatus("DONE");
            job.setSeededCards(seeded);
            job.setFinishedAt(LocalDateTime.now());
            log(job, "[DONE] Seeded " + seeded + " cards for deck: " + cfg.nameEn());

        } catch (Exception e) {
            job.setStatus("ERROR");
            job.setErrorMsg(e.getMessage());
            job.setFinishedAt(LocalDateTime.now());
            log(job, "[ERROR] " + e.getMessage());
        }
        jobRepo.save(job);
    }

    // ── Step 1: fetch from tarotapi.dev ────────────────────────

    private List<Map<String, Object>> fetchCards(CrawlJob job) throws Exception {
        String url = "https://tarotapi.dev/api/v1/cards";
        String body = httpGet(url);
        JsonNode root = mapper.readTree(body);
        JsonNode arr  = root.get("cards");
        if (arr == null || !arr.isArray()) throw new RuntimeException("Unexpected response from tarotapi.dev");

        List<Map<String, Object>> result = new ArrayList<>();
        int idx = 0;
        for (JsonNode raw : arr) {
            result.add(normalizeCard(idx++, raw));
        }
        return result;
    }

    private Map<String, Object> normalizeCard(int idx, JsonNode raw) {
        String nameEn = raw.path("name").asText("");
        String arcana = raw.path("type").asText("major").toLowerCase().contains("major") ? "major" : "minor";
        String suit   = raw.path("suit").asText("").toLowerCase();
        if (suit.isBlank()) suit = null;

        Integer number = null;
        try { number = raw.path("value_int").asInt(); } catch (Exception ignored) {}

        // Vietnamese name
        String nameVi;
        if ("major".equals(arcana)) {
            int n = number != null ? number : idx;
            nameVi = (n >= 0 && n < MAJOR_VI.length) ? MAJOR_VI[n] : nameEn;
        } else {
            String[] parts = nameEn.split(" of ");
            String rank = parts.length > 0 ? parts[0].trim() : "";
            String rankVi = RANK_VI.getOrDefault(rank, RANK_VI2.getOrDefault(rank, rank));
            String suitVi = suit != null ? SUIT_VI.getOrDefault(suit, suit) : "";
            nameVi = (rankVi + " " + suitVi).trim();
        }

        // Keywords / meanings
        String kwUp = "", kwRev = "", mnUp = "", mnRev = "";
        JsonNode kwNode = raw.get("keywords_upright");
        if (kwNode != null && kwNode.isArray()) {
            List<String> kws = new ArrayList<>();
            kwNode.forEach(k -> kws.add(k.asText()));
            kwUp = String.join(", ", kws);
        }
        JsonNode kwRevNode = raw.get("keywords_reversed");
        if (kwRevNode != null && kwRevNode.isArray()) {
            List<String> kws = new ArrayList<>();
            kwRevNode.forEach(k -> kws.add(k.asText()));
            kwRev = String.join(", ", kws);
        }
        JsonNode meaning = raw.get("meaning");
        if (meaning != null && meaning.isObject()) {
            mnUp  = meaning.path("up").asText("");
            mnRev = meaning.path("rev").asText("");
        }
        if (mnUp.isBlank())  mnUp  = raw.path("meaning_upright").asText("");
        if (mnRev.isBlank()) mnRev = raw.path("meaning_reversed").asText("");

        String element = suit != null ? switch(suit) {
            case "wands" -> "Fire"; case "cups" -> "Water";
            case "swords" -> "Air"; case "pentacles" -> "Earth";
            default -> "";
        } : majorElement(nameEn);

        Map<String, Object> card = new LinkedHashMap<>();
        card.put("cardIndex",        idx);
        card.put("nameVi",           nameVi);
        card.put("nameEn",           nameEn);
        card.put("arcana",           arcana);
        card.put("suit",             suit);
        card.put("number",           number);
        card.put("imageUrl",         null);
        card.put("thumbnailUrl",     null);
        card.put("keywordsUpright",  kwUp);
        card.put("keywordsReversed", kwRev);
        card.put("meaningUpright",   mnUp);
        card.put("meaningReversed",  mnRev);
        card.put("description",      raw.path("desc").asText(""));
        card.put("element",          element);
        card.put("planetOrSign",     raw.path("planet").asText(""));
        card.put("numerologyLink",   "major".equals(arcana) ? number : null);
        return card;
    }

    // ── Step 2: enrich from ekelen/tarot-json ──────────────────

    private void enrichCards(List<Map<String, Object>> cards, CrawlJob job) {
        try {
            String url  = "https://raw.githubusercontent.com/ekelen/tarot-json/master/data/tarot-images.json";
            String body = httpGet(url);
            JsonNode root = mapper.readTree(body);
            JsonNode arr  = root.get("cards");
            if (arr == null) return;

            Map<String, JsonNode> lookup = new HashMap<>();
            arr.forEach(c -> lookup.put(c.path("name").asText(""), c));

            int enriched = 0;
            for (Map<String, Object> card : cards) {
                JsonNode e = lookup.get(card.get("nameEn"));
                if (e == null) continue;

                if (isBlank(card.get("keywordsUpright"))) {
                    JsonNode kw = e.get("keywords");
                    if (kw != null && kw.isArray()) {
                        List<String> kws = new ArrayList<>();
                        kw.forEach(k -> kws.add(k.asText()));
                        card.put("keywordsUpright", String.join(", ", kws));
                    }
                }
                if (isBlank(card.get("meaningUpright"))) {
                    card.put("meaningUpright",  e.path("meanings").path("light").asText(""));
                    card.put("meaningReversed", e.path("meanings").path("shadow").asText(""));
                }
                if (isBlank(card.get("element"))) {
                    card.put("element", e.path("elemental").asText(""));
                }
                if (isBlank(card.get("description"))) {
                    String arch  = e.path("archetype").asText("");
                    String myth  = e.path("mythological_themes").asText("");
                    card.put("description", arch.isBlank() ? myth : arch + (myth.isBlank() ? "" : "\n" + myth));
                }
                enriched++;
            }
            log(job, "      Enriched " + enriched + " cards");
        } catch (Exception e) {
            log(job, "      Enrichment failed (non-fatal): " + e.getMessage());
        }
    }

    // ── Step 3: images ─────────────────────────────────────────

    private void uploadImages(List<Map<String, Object>> cards, String deckSlug, CrawlJob job) {
        int uploaded = 0, skipped = 0, failed = 0;
        for (Map<String, Object> card : cards) {
            int idx = (Integer) card.get("cardIndex");
            String filename = WIKIMEDIA.get(idx);
            if (filename == null) { failed++; continue; }

            String b2Key = "tarot/" + deckSlug + "/" + String.format("%02d", idx) + "-"
                    + slugify((String) card.get("nameEn")) + ".jpg";

            try {
                // Resolve Wikimedia direct URL
                String imgUrl = resolveWikimediaUrl(filename);
                if (imgUrl == null) { failed++; continue; }

                String cdnUrl = imageStorage.uploadFromUrl(imgUrl, b2Key);
                if (cdnUrl != null) {
                    card.put("imageUrl",     cdnUrl);
                    card.put("thumbnailUrl", cdnUrl);
                    uploaded++;
                } else {
                    failed++;
                }
            } catch (Exception e) {
                log(job, "      [" + idx + "] image error: " + e.getMessage());
                failed++;
            }

            // Polite rate limiting
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
        }
        log(job, "      Images: " + uploaded + " uploaded, " + skipped + " skipped, " + failed + " failed");
    }

    private String resolveWikimediaUrl(String filename) throws Exception {
        String api = "https://commons.wikimedia.org/w/api.php"
                + "?action=query&titles=File:" + filename
                + "&prop=imageinfo&iiprop=url&format=json";
        String body = httpGet(api);
        JsonNode pages = mapper.readTree(body).path("query").path("pages");
        for (JsonNode page : pages) {
            JsonNode info = page.path("imageinfo");
            if (info.isArray() && info.size() > 0) {
                return info.get(0).path("url").asText(null);
            }
        }
        return null;
    }

    // ── Step 4: seed into DB ────────────────────────────────────

    private int seedDeck(DeckConfig cfg, List<Map<String, Object>> cards,
                         boolean update, CrawlJob job) {
        // Resolve or create deck
        Optional<Deck> existing = deckRepo.findAll().stream()
                .filter(d -> d.getNameEn().equalsIgnoreCase(cfg.nameEn()))
                .findFirst();

        Deck deck;
        if (existing.isPresent() && update) {
            deck = existing.get();
            cardRepo.deleteAll(cardRepo.findByDeckId(deck.getId()));
            log(job, "      Existing deck found — replacing cards");
        } else if (existing.isPresent()) {
            log(job, "      Deck already exists, skipping (use update=true to replace)");
            return 0;
        } else {
            deck = new Deck();
        }

        deck.setModule("tarot");
        deck.setNameVi(cfg.nameVi());
        deck.setNameEn(cfg.nameEn());
        deck.setDescription(cfg.description());
        deck.setCardCount(cfg.cardCount());
        deck.setStyle(cfg.style());
        deck.setActive(true);
        if (!cards.isEmpty() && cards.get(0).get("imageUrl") != null) {
            deck.setCoverImageUrl((String) cards.get(0).get("imageUrl"));
        }
        deck = deckRepo.save(deck);

        int count = 0;
        for (Map<String, Object> cp : cards) {
            Card c = new Card();
            c.setDeck(deck);
            c.setCardIndex((Integer) cp.get("cardIndex"));
            c.setNameVi(str(cp.get("nameVi"), str(cp.get("nameEn"), "")));
            c.setNameEn(str(cp.get("nameEn"), ""));
            c.setArcana(str(cp.get("arcana"), "minor"));
            c.setSuit((String) cp.get("suit"));
            c.setNumber((Integer) cp.get("number"));
            c.setImageUrl((String) cp.get("imageUrl"));
            c.setThumbnailUrl((String) cp.get("thumbnailUrl"));
            c.setKeywordsUpright(str(cp.get("keywordsUpright"), ""));
            c.setKeywordsReversed(str(cp.get("keywordsReversed"), ""));
            c.setMeaningUpright(str(cp.get("meaningUpright"), ""));
            c.setMeaningReversed(str(cp.get("meaningReversed"), ""));
            c.setDescription(str(cp.get("description"), ""));
            c.setElement(str(cp.get("element"), ""));
            c.setPlanetOrSign(str(cp.get("planetOrSign"), ""));
            c.setNumerologyLink((Integer) cp.get("numerologyLink"));
            cardRepo.save(c);
            count++;
        }
        return count;
    }

    // ── Helpers ─────────────────────────────────────────────────

    private String httpGet(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "CelestialBot/1.0")
                .GET().build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200)
            throw new RuntimeException("HTTP " + resp.statusCode() + " for " + url);
        return resp.body();
    }

    private void log(CrawlJob job, String line) {
        System.out.println("[CrawlService] " + line);
        String existing = job.getLog() != null ? job.getLog() : "";
        job.setLog(existing + "\n" + line);
        jobRepo.save(job);
    }

    private static String slugify(String s) {
        return s.toLowerCase().replaceAll("[^a-z0-9]", "-").replaceAll("-+", "-").substring(0, Math.min(s.length(), 28));
    }

    private static boolean isBlank(Object o) {
        return o == null || o.toString().isBlank();
    }

    private static String str(Object o, String fallback) {
        return o == null || o.toString().isBlank() ? fallback : o.toString();
    }

    private static String majorElement(String name) {
        return switch (name) {
            case "The Fool","The Magician","The Lovers","The Star" -> "Air";
            case "The High Priestess","The Hanged Man","Death","The Moon" -> "Water";
            case "The Empress","The Emperor","The Hierophant","The Hermit","The Devil","The World" -> "Earth";
            case "The Chariot","Strength","Wheel of Fortune","Temperance","The Tower","The Sun","Judgement" -> "Fire";
            default -> "";
        };
    }
}
