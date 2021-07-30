package com.github.ehsan1222.ca.services;

import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.github.ehsan1222.ca.crypto.BinanceCryptoMarket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.github.ehsan1222.ca.constants.KafkaConstants.BTC_TOPIC_NAME;
import static com.github.ehsan1222.ca.constants.KafkaConstants.ETH_TOPIC_NAME;

@Service
public class RetrieveCryptoProcess {

    private static final int TIME_INTERVAL_IN_MILLIS = 60000;
    private static final Integer NUM_CANDLESTICK_BARS = 100;

    @Value("${BINANCE.APIKEY}")
    private String apiKey;

    @Value("${BINANCE.SECRET_KEY}")
    private String secretKey;

    private final CryptoSenderService cryptoSenderService;

    public RetrieveCryptoProcess(CryptoSenderService cryptoSenderService) {
        this.cryptoSenderService = cryptoSenderService;
    }

    @Scheduled(fixedRate = TIME_INTERVAL_IN_MILLIS)
    public void btcCandlestickBarsSchedule() {
        List<Candlestick> btcCandlesticks = getCandlesticks("BTCBUSD");
        cryptoSenderService.sendMessage(BTC_TOPIC_NAME, btcCandlesticks);
    }

    @Scheduled(fixedRate = TIME_INTERVAL_IN_MILLIS)
    public void ethCandlestickBarsSchedule() {
        List<Candlestick> ethCandlesticks = getCandlesticks("ETHBUSD");
        cryptoSenderService.sendMessage(ETH_TOPIC_NAME, ethCandlesticks);
    }

    private List<Candlestick> getCandlesticks(String symbol) {
        BinanceCryptoMarket market = new BinanceCryptoMarket(apiKey, secretKey);
        return market.getCandlesticks(symbol, CandlestickInterval.ONE_MINUTE, NUM_CANDLESTICK_BARS);
    }
}
