package com.digitalft.match.api;

import java.math.BigDecimal;

/**
 * Orders related to a specific Instrument.
 */
public interface InstrumentOrders {

    OrderList getBuyOrders();
    OrderList getSellOrders();

    OpenInterest getOpenInterest();

    BigDecimal getAverageExecutionPrice();

}
