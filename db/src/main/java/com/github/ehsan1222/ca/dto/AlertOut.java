package com.github.ehsan1222.ca.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlertOut {
    private long id;
    private String rule;
    private String market;
    private double price;
    private LocalDateTime dataDateTime;
}
