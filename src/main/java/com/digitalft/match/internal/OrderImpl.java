package com.digitalft.match.internal;

import com.digitalft.match.api.Order;

import java.math.BigDecimal;

/**
 * An Order.
 */
public class OrderImpl implements Order {

    private final String instrumentCode;
    private final boolean buy;
    private final BigDecimal price;
    private final int quantity;
    private final String user;
    private final long timestamp;

    public OrderImpl(String instrumentCode, boolean buy, BigDecimal price, int quantity, String user, long timestamp) {
        this.instrumentCode = instrumentCode;
        this.buy = buy;
        this.price = price;
        this.quantity = quantity;
        this.user = user;
        this.timestamp = timestamp;
    }

    @Override
    public OrderImpl withNewQuantity(int updatedQuantity) {
        return new OrderImpl(instrumentCode,buy,price,updatedQuantity,user,timestamp);
    }

    @Override
    public String getInstrumentCode() {
        return instrumentCode;
    }

    @Override
    public boolean isBuyNotSell() {
        return buy;
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
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "OrderImpl{" +
                "instrumentCode='" + instrumentCode + '\'' +
                ", buy=" + buy +
                ", price=" + price +
                ", quantity=" + quantity +
                ", user='" + user + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

}
