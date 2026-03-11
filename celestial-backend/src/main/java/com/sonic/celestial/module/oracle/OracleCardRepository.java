package com.sonic.celestial.module.oracle;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OracleCardRepository extends JpaRepository<OracleCard, Long> {
    List<OracleCard> findByDeckId(Long deckId);
}