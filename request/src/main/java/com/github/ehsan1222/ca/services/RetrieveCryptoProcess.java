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
    private static final Integer FIRST_CANDLESTICK_BATCH = 100;

    @Value("${BINANCE.APIKEY}")
    private String apiKey;

    @Value("${BINANCE.SECRET_KEY}")
    private String secretKey;

    private boolean isFirstBtcRequest = true;
    private boolean isFirstEthRequest = true;

    private final CryptoSenderService cryptoSenderService;

    public RetrieveCryptoProcess(CryptoSenderService cryptoSenderService) {
        this.cryptoSenderService = cryptoSenderService;
    }

    @Scheduled(fixedRate = TIME_INTERVAL_IN_MILLIS)
    public void btcCandlestickBarsSchedule() {
        int numberOfCandlestickBars;
        if (isFirstBtcRequest) {
            numberOfCandlestickBars = FIRST_CANDLESTICK_BATCH;
            isFirstBtcRequest = false;
        } else {
            numberOfCandlestickBars = 1;
        }
        List<Candlestick> btcCandlesticks = getCandlesticks("BTCBUSD", numberOfCandlestickBars);
        cryptoSenderService.sendMessage(BTC_TOPIC_NAME, btcCandlesticks);
    }

    @Scheduled(fixedRate = TIME_INTERVAL_IN_MILLIS)
    public void ethCandlestickBarsSchedule() {
        int numberOfCandlestickBars;
        if (isFirstEthRequest) {
            numberOfCandlestickBars = FIRST_CANDLESTICK_BATCH;
            isFirstEthRequest = false;
        } else {
            numberOfCandlestickBars = 1;
        }
        List<Candlestick> ethCandlesticks = getCandlesticks("ETHBUSD", numberOfCandlestickBars);
        cryptoSenderService.sendMessage(ETH_TOPIC_NAME, ethCandlesticks);
    }

    private List<Candlestick> getCandlesticks(String symbol, int numberOfCandlestickBars) {
        BinanceCryptoMarket market = new BinanceCryptoMarket(apiKey, secretKey);
        return market.getCandlesticks(symbol, CandlestickInterval.ONE_MINUTE, numberOfCandlestickBars);
    }
}
