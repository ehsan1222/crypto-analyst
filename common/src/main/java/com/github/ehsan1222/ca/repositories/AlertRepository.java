package com.github.ehsan1222.ca.repositories;

import com.github.ehsan1222.ca.entities.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Long> {
}
