package com.digitalft.match.api;

import java.math.BigDecimal;

/**
 * Interest at a Price Level
 */
public interface PriceLevel {

    BigDecimal getPrice();
    int getQuantity();

}
