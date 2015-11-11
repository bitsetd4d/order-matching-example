package com.digitalft.match.internal;


import com.digitalft.match.api.Execution;

import java.math.BigDecimal;

/**
 * An Execution.
 */
public class ExecutionImpl implements Execution {

    private final String instrumentCode;
    private final BigDecimal price;
    private final int quantity;
    private final String user;

    public ExecutionImpl(String instrumentCode, BigDecimal price, int quantity, String user) {
        this.instrumentCode = instrumentCode;
        this.price = price;
        this.quantity = quantity;
        this.user = user;
    }

    @Override
    public String getInstrumentCode() {
        return instrumentCode;
    }

    @Override
    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "ExecutionImpl{" +
                "instrumentCode='" + instrumentCode + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", user='" + user + '\'' +
                '}';
    }

}
