package com.digitalft.match.api;

import java.math.BigDecimal;

/**
 * An Order Execution
 */
public interface Execution {

    String getInstrumentCode();
    BigDecimal getPrice();
    int getQuantity();
    String getUser();

}
