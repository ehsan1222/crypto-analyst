package com.github.ehsan1222.ca.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "alert")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rule;

    private String market;

    private double price;

    @Column(name = "close_date")
    private LocalDateTime closeDate;

    public Alert(String rule, String market, double price, LocalDateTime closeDate) {
        this.rule = rule;
        this.market = market;
        this.price = price;
        this.closeDate = closeDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alert alert = (Alert) o;
        return Objects.equals(rule, alert.rule) &&
                Objects.equals(market, alert.market) &&
                Objects.equals(closeDate, alert.closeDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rule, market, closeDate);
    }
}
