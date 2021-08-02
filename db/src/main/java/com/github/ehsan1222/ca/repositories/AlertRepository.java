package com.github.ehsan1222.ca.repositories;

import com.github.ehsan1222.ca.entities.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    @Query("SELECT p FROM Alert p ORDER BY p.closeDate DESC")
    List<Alert> findAllOrderByCloseDateDesc();

    Optional<Alert> findByRuleAndMarketAndCloseDate(String rule, String market, LocalDateTime dateTime);
}
