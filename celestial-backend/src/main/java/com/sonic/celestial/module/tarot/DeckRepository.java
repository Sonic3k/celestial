package com.sonic.celestial.module.tarot;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DeckRepository extends JpaRepository<Deck, Long> {
    List<Deck> findByModuleAndActiveTrue(String module);
}