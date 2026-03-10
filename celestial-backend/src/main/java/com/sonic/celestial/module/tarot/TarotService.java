package com.sonic.celestial.module.tarot;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TarotService {

    private static final String[] CELTIC_POSITIONS = {
        "Tình Huống Hiện Tại", "Thách Thức / Cản Trở",
        "Nền Tảng / Quá Khứ Xa", "Quá Khứ Gần",
        "Khả Năng Tốt Nhất", "Tương Lai Gần",
        "Bản Thân Bạn", "Môi Trường Xung Quanh",
        "Hy Vọng & Nỗi Sợ", "Kết Quả Cuối Cùng"
    };

    private final DeckRepository deckRepo;
    private final CardRepository cardRepo;
    private final Random rng = new Random();

    public TarotService(DeckRepository deckRepo, CardRepository cardRepo) {
        this.deckRepo = deckRepo;
        this.cardRepo = cardRepo;
    }

    public List<DeckDto> listDecks() {
        return deckRepo.findByModuleAndActiveTrue("tarot")
                .stream().map(DeckDto::from).collect(Collectors.toList());
    }

    public TarotDrawResult draw(TarotDrawRequest req) {
        // Resolve deck
        Deck deck = resolveDeck(req.getDeckId());

        // Determine spread size
        int count = switch (req.getSpread()) {
            case "3"      -> 3;
            case "celtic" -> 10;
            default       -> 1;
        };

        // Draw random cards (no duplicates)
        List<Card> pool = cardRepo.findByDeckId(deck.getId());
        if (pool.size() < count) {
            throw new IllegalStateException("Deck không đủ cards để rút " + count + " lá.");
        }
        Collections.shuffle(pool, rng);
        List<Card> drawn = pool.subList(0, count);

        // Build result
        TarotDrawResult result = new TarotDrawResult();
        result.deckId   = deck.getId();
        result.deckName = deck.getNameEn();
        result.spread   = req.getSpread();
        result.cards    = new ArrayList<>();

        String[] positions = getPositions(req.getSpread(), count);

        for (int i = 0; i < drawn.size(); i++) {
            boolean reversed = rng.nextBoolean();
            TarotDrawResult.SpreadCard sc = new TarotDrawResult.SpreadCard();
            sc.position = positions[i];
            sc.card     = CardDto.from(drawn.get(i), reversed);
            result.cards.add(sc);
        }

        return result;
    }

    public CardDto getCard(Long id) {
        Card c = cardRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Card not found: " + id));
        return CardDto.from(c, false);
    }

    // ── Helpers ────────────────────────────────────────────────

    private Deck resolveDeck(Long deckId) {
        if (deckId != null) {
            return deckRepo.findById(deckId)
                    .orElseThrow(() -> new NoSuchElementException("Deck not found: " + deckId));
        }
        return deckRepo.findByModuleAndActiveTrue("tarot")
                .stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No active tarot deck found."));
    }

    private String[] getPositions(String spread, int count) {
        return switch (spread) {
            case "3"      -> new String[]{"Quá Khứ", "Hiện Tại", "Tương Lai"};
            case "celtic" -> CELTIC_POSITIONS;
            default       -> new String[]{"Lá Bài"};
        };
    }
}