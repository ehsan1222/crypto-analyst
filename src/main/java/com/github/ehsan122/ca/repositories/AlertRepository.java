package com.github.ehsan122.ca.repositories;

import com.github.ehsan122.ca.entities.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Long> {
}
