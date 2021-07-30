package com.github.ehsan1222.ca.repositories;

import com.github.ehsan1222.ca.entities.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    @Query("SELECT p FROM Alert p ORDER BY p.closeDate DESC")
    List<Alert> findAllOrderByCloseDateDesc();

}
