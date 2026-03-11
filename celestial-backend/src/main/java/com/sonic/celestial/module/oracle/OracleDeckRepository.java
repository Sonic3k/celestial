package com.sonic.celestial.module.oracle;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OracleDeckRepository extends JpaRepository<OracleDeck, Long> {
    List<OracleDeck> findByActiveTrue();
}