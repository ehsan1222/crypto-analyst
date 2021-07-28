package com.github.ehsan122.ca.entities;

import lombok.*;

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

    @Column(name = "date_calculated")
    private LocalDateTime dateCalculated;

    public Alert(String rule, String market, double price, LocalDateTime dateCalculated) {
        this.rule = rule;
        this.market = market;
        this.price = price;
        this.dateCalculated = dateCalculated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alert that = (Alert) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
