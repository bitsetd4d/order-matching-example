package com.digitalft.match.internal;

import com.digitalft.match.api.PriceLevel;

import java.math.BigDecimal;

/**
 * Price Level
 */
public class PriceLevelImpl implements PriceLevel {

    private final BigDecimal price;
    private int quantity;

    public PriceLevelImpl(BigDecimal price) {
        this.price = price;
    }

    @Override
    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    void recordBuy(int amount) {
        quantity += amount;
    }

    void recordSell(int amount) {
        quantity -= amount;
    }

    @Override
    public String toString() {
        return "PriceLevelImpl{" +
                "price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
