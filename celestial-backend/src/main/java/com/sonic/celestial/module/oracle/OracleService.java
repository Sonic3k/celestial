package com.sonic.celestial.module.oracle;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class OracleService {

    private final OracleDeckRepository deckRepo;
    private final OracleCardRepository cardRepo;
    private final Random rng = new Random();

    public OracleService(OracleDeckRepository deckRepo, OracleCardRepository cardRepo) {
        this.deckRepo = deckRepo;
        this.cardRepo = cardRepo;
    }

    public List<OracleDeckDto> listDecks() {
        return deckRepo.findByActiveTrue()
                .stream().map(OracleDeckDto::from).collect(Collectors.toList());
    }

    public OracleDrawResult draw(OracleDrawRequest req) {
        OracleDeck deck = resolveDeck(req.getDeckId());

        List<OracleCard> pool = cardRepo.findByDeckId(deck.getId());
        if (pool.isEmpty()) {
            throw new IllegalStateException("Oracle deck has no cards.");
        }

        OracleCard drawn = pool.get(rng.nextInt(pool.size()));

        OracleDrawResult result = new OracleDrawResult();
        result.deckId   = deck.getId();
        result.deckName = deck.getNameEn();
        result.question = req.getQuestion();
        result.card     = OracleCardDto.from(drawn);
        return result;
    }

    private OracleDeck resolveDeck(Long deckId) {
        if (deckId != null) {
            return deckRepo.findById(deckId)
                    .orElseThrow(() -> new NoSuchElementException("Oracle deck not found: " + deckId));
        }
        return deckRepo.findByActiveTrue().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No active oracle deck found."));
    }
}