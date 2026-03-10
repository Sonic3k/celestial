package com.sonic.celestial.module.tarot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByDeckId(Long deckId);

    @Query("SELECT c FROM Card c WHERE c.deck.id = :deckId ORDER BY RANDOM() LIMIT :n")
    List<Card> findRandomByDeckId(@Param("deckId") Long deckId, @Param("n") int n);

    int countByDeckId(Long deckId);
}