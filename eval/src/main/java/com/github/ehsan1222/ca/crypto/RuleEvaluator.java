package com.github.ehsan1222.ca.crypto;

import com.binance.api.client.domain.market.Candlestick;
import com.github.ehsan1222.ca.dao.Rule;
import com.github.ehsan1222.ca.dao.RuleType;
import com.github.ehsan1222.ca.services.AlertService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class RuleEvaluator {

    private final AlertService alertService;

    public RuleEvaluator(AlertService alertService) {
        this.alertService = alertService;
    }

    public void evaluate(List<Candlestick> candlesticks, List<Rule> rules) {
        if (rules == null || rules.size() == 0) {
            return;
        }
        for (Rule rule: rules) {
            evaluate(candlesticks, rule);
        }
    }

    public void evaluate(List<Candlestick> candlesticks, Rule rule) {
        if (rule == null) {
            return;
        }
        try {
            double firstMeanValue = getMeanValue(candlesticks, rule.getFirstInterval(), rule.getType());
            double lastMeanValue = getMeanValue(candlesticks, rule.getLastInterval(), rule.getType());
            if (isEvaluate(firstMeanValue, lastMeanValue, rule.getCheck())) {
                System.out.println(candlesticks);
                Candlestick lastCandlestick = candlesticks.get(candlesticks.size() - 1);
                double currentPrice = Double.parseDouble(lastCandlestick.getClose());
                alertService.save(rule.getName(), rule.getMarketName(), currentPrice, lastCandlestick.getCloseTime());
            }
        } catch (IllegalStateException e) {

        }
    }

    private boolean isEvaluate(Double first, Double last, Integer check) {
        if (check > 0) {
            return first.compareTo(last) > 0;
        } else if (check < 0) {
            return first.compareTo(last) < 0;
        } else {
            return Objects.equals(first, last);
        }
    }

    public double getMeanValue(List<Candlestick> candlesticks, int interval, RuleType ruleType) {
        if (interval < 1 || interval > candlesticks.size()) {
            throw new IllegalStateException();
        }
        double mean = 0;
        for (int i = candlesticks.size() - 1; i >= candlesticks.size() - interval; i--) {
            Candlestick currentCandlestick = candlesticks.get(i);
            mean += getValue(currentCandlestick, ruleType);
        }
        mean /= interval;
        return mean;
    }

    private double getValue(Candlestick candlestick, RuleType type) {
        switch (type) {
            case LOW:
                return Double.parseDouble(candlestick.getLow());
            case HIGH:
                return Double.parseDouble(candlestick.getHigh());
            case OPEN:
                return Double.parseDouble(candlestick.getOpen());
            case CLOSE:
                return Double.parseDouble(candlestick.getClose());
        }
        return 0;
    }

}
