package com.github.ehsan1222.ca.crypto;

import com.binance.api.client.domain.market.Candlestick;
import com.github.ehsan1222.ca.dao.Pattern;
import com.github.ehsan1222.ca.dao.PatternCheck;
import com.github.ehsan1222.ca.dao.PatternType;
import com.github.ehsan1222.ca.services.AlertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RuleEvaluator {

    private final AlertService alertService;

    public RuleEvaluator(AlertService alertService) {
        this.alertService = alertService;
    }

    public void evaluate(List<Candlestick> candlesticks, List<Pattern> patterns) {
        if (patterns == null || patterns.size() == 0) {
            log.info("there is not any pattern to evaluate");
            return;
        }
        for (Pattern pattern : patterns) {
            evaluate(candlesticks, pattern);
        }
    }

    protected void evaluate(List<Candlestick> candlesticks, Pattern pattern) {
        if (pattern == null) {
            log.warn("null pattern was entered");
            return;
        }
        if (haveEnoughCandlesticksItems(candlesticks, pattern)) {
            double firstMeanValue = getMeanValue(candlesticks, pattern.getFirstInterval(), pattern.getType());
            double lastMeanValue = getMeanValue(candlesticks, pattern.getLastInterval(), pattern.getType());
            log.info("firstMeanValue = " + firstMeanValue + ", lastMeanValue = " + lastMeanValue + ", candlestick size = " + candlesticks.size());
            if (isEvaluate(firstMeanValue, lastMeanValue, pattern.getCheck())) {
                Candlestick lastCandlestick = candlesticks.get(candlesticks.size() - 1);
                double currentPrice = Double.parseDouble(lastCandlestick.getClose());
                alertService.save(pattern.getRule(), pattern.getMarketName(), currentPrice, lastCandlestick.getCloseTime());
            }
        } else {
            log.info(String.format("candlestick hasn't enough items because candlestick size is %d but pattern interval is %d",
                    candlesticks.size(), Math.max(pattern.getFirstInterval(), pattern.getLastInterval())));
        }
    }

    protected boolean haveEnoughCandlesticksItems(List<Candlestick> candlesticks, Pattern pattern) {
        return candlesticks.size() > pattern.getFirstInterval() &&
                candlesticks.size() > pattern.getLastInterval();
    }

    protected boolean isEvaluate(double first, double last, PatternCheck check) {
        switch (check) {
            case LESS_THAN:
                return first < last;
            case LESS_THAN_EQUAL:
                return first <= last;
            case EQUAL:
                return first == last;
            case GREATER_THAN:
                return first > last;
            case GREATER_THAN_EQUAL:
                return first >= last;
        }
        return false;
    }

    protected double getMeanValue(List<Candlestick> candlesticks, int interval, PatternType patternType) {
        if (interval < 1 || interval > candlesticks.size()) {
            throw new IllegalStateException("invalid interval range");
        }
        double mean = 0;
        for (int i = candlesticks.size() - 1; i >= candlesticks.size() - interval; i--) {
            Candlestick currentCandlestick = candlesticks.get(i);
            mean += getValue(currentCandlestick, patternType);
        }
        mean /= interval;
        return mean;
    }

    protected double getValue(Candlestick candlestick, PatternType type) {
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
        throw new UnsupportedOperationException();
    }

}
