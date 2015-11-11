package com.digitalft.match.internal;

import com.digitalft.match.api.OpenInterest;
import com.digitalft.match.api.Order;
import com.digitalft.match.api.PriceLevel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Open Interest
 */
public class OpenInterestImpl implements OpenInterest {

    private final String instrumentCode;
    private final List<PriceLevel> levels;

    public OpenInterestImpl(String instrumentCode, List<PriceLevel> levels) {
        this.instrumentCode = instrumentCode;
        this.levels = levels;
    }

    public static OpenInterest newInstance(String instrumentCode, List<Order> sellOrders, List<Order> buyOrders) {
        Map<BigDecimal,PriceLevelImpl> levels = new LinkedHashMap<>();
        buyOrders.forEach(order -> {
            PriceLevelImpl level = levels.computeIfAbsent(order.getPrice(), price -> new PriceLevelImpl(price));
            level.recordBuy(order.getQuantity());
        });
        sellOrders.forEach(order -> {
            PriceLevelImpl level = levels.computeIfAbsent(order.getPrice(),price -> new PriceLevelImpl(price));
            level.recordSell(order.getQuantity());
        });
        return new OpenInterestImpl(instrumentCode,new ArrayList<>(levels.values()));
    }

    @Override
    public String getInstrumentCode() {
        return instrumentCode;
    }

    @Override
    public List<PriceLevel> getLevels() {
        return levels;
    }


}
