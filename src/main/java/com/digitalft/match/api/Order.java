package com.digitalft.match.api;

import com.digitalft.match.internal.OrderImpl;

import java.math.BigDecimal;

/**
 * An Order
 */
public interface Order {

    static Order newBuy(String ric, BigDecimal price, int quantity, String user) {
        return new OrderImpl(ric,true,price,quantity,user,System.nanoTime());
    }

    static Order newSell(String ric, BigDecimal price, int quantity, String user) {
        return new OrderImpl(ric,false,price,quantity,user,System.nanoTime());
    }

    Order withNewQuantity(int remaining);

    String getInstrumentCode();
    boolean isBuyNotSell();
    BigDecimal getPrice();
    int getQuantity();
    String getUser();

    long getTimestamp();


}
