package com.github.ehsan1222.ca.crypto;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;

import java.util.List;

public class BinanceCryptoMarket {
    private final BinanceApiRestClient restClient;

    public BinanceCryptoMarket(String apiKey, String secretKey) {
        var factory = BinanceApiClientFactory.newInstance(apiKey, secretKey);
        this.restClient = factory.newRestClient();
    }

    public List<Candlestick> getCandlesticks(String symbol, CandlestickInterval interval, int limit) {
        return restClient.getCandlestickBars(symbol, interval, limit, null, null);
    }

}
