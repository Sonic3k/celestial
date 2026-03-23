package com.sonic.celestial.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CrawlJobRepository extends JpaRepository<CrawlJob, Long> {
    List<CrawlJob> findTop20ByOrderByCreatedAtDesc();
    List<CrawlJob> findByDeckSlugOrderByCreatedAtDesc(String deckSlug);
}
