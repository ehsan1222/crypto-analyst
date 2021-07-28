package com.github.ehsan122.ca.controllers;

import com.github.ehsan122.ca.dto.AlertOut;
import com.github.ehsan122.ca.exceptions.AlertNotFoundException;
import com.github.ehsan122.ca.services.AlertService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public ResponseEntity<List<AlertOut>> getAllAlerts() {
        List<AlertOut> alertOutList = alertService.getAll();
        return ResponseEntity.ok(alertOutList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertOut> getAlert(@PathVariable("id") Long id) {
        try{
            AlertOut alertOut = alertService.get(id);
            return ResponseEntity.ok(alertOut);
        } catch (AlertNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

}
