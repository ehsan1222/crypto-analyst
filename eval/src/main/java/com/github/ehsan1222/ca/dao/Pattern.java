package com.github.ehsan1222.ca.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Pattern {

    private String rule;

    @JsonProperty("market_name")
    private String marketName;

    private PatternType type;

    @JsonProperty("first_interval")
    private Integer firstInterval;

    @JsonProperty("last_interval")
    private Integer lastInterval;

    private PatternCheck check;

}
