package com.github.ehsan1222.ca.services;

import com.github.ehsan1222.ca.dto.AlertOut;
import com.github.ehsan1222.ca.entities.Alert;
import com.github.ehsan1222.ca.exceptions.AlertNotFoundException;
import com.github.ehsan1222.ca.repositories.AlertRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AlertService {

    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public AlertOut save(String rule, String market, double price, Long openDateInMillis) {
        if (rule == null || rule.isBlank() || market == null || market.isBlank() || openDateInMillis == null) {
            throw new IllegalArgumentException();
        }
        Alert alert = new Alert(rule, market, price, convertMillisToDateTime(openDateInMillis));
        Alert savedAlert = alertRepository.save(alert);
        return convertToDto(savedAlert);
    }

    public AlertOut get(Long id) {
        return alertRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new AlertNotFoundException(String.format("alert %d not found", id)));
    }

    public List<AlertOut> getAll() {
        return alertRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private LocalDateTime convertMillisToDateTime(Long millis) {
        return Instant.ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private AlertOut convertToDto(Alert alert) {
        return new AlertOut(alert.getId(),
                alert.getRule(),
                alert.getMarket(),
                alert.getPrice(),
                alert.getDateCalculated());
    }
}
