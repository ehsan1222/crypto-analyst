package com.github.ehsan1222.ca.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Rule {

    private String name;

    @JsonProperty("market_name")
    private String marketName;

    private RuleType type;

    @JsonProperty("first_interval")
    private Integer firstInterval;

    @JsonProperty("last_interval")
    private Integer lastInterval;

    private Integer check;

}
