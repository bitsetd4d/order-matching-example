package com.digitalft.match.api;

import java.util.List;

/**
 * Report open interest in an instrument
 */
public interface OpenInterest {

    String getInstrumentCode();
    List<PriceLevel> getLevels();
}
