package com.digitalft.match.internal;

import com.digitalft.match.api.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Orders related to a particular instrument.
 */
public class InstrumentOrdersImpl implements InstrumentOrders {

    private static Logger logger = Logger.getLogger(InstrumentOrdersImpl.class.getName());

    private static Comparator<Order> BUY = createBuyComparitor();
    private static Comparator<Order> SELL = createSellComparitor();

    private final String instrumentCode;
    private final OrderListImpl buys = new OrderListImpl(BUY);
    private final OrderListImpl sells = new OrderListImpl(SELL);

    private BigDecimal averageExecution = BigDecimal.ZERO;
    private long totalQuantity;

    private Object orderLock = new Object();

    public InstrumentOrdersImpl(String instrumentCode) {
        this.instrumentCode = instrumentCode;
    }

    @Override
    public OrderList getBuyOrders() {
        return buys;
    }

    @Override
    public OrderList getSellOrders() {
        return sells;
    }

    // ------------------------------------------------------------------------
    // Order execution
    // ------------------------------------------------------------------------
    public void submit(Order order,OrderMatcherListener listener) {
        OrderListImpl list = order.isBuyNotSell() ? buys : sells;
        synchronized (orderLock) {
            list.add(order);
            matchOrders(listener);
        }
    }

    private void matchOrders(OrderMatcherListener listener) {
        Order bestBuy = buys.getBest();
        Order bestSell = sells.getBest();
        while (bestBuy != null && bestSell != null && bestSell.getPrice().compareTo(bestBuy.getPrice()) <= 0) {
            execute(bestBuy,bestSell,listener);
            bestBuy = buys.getBest();
            bestSell = sells.getBest();
        }
    }

    private void execute(Order buy, Order sell, OrderMatcherListener listener) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Matching Buy: "+buy+" Sell: "+sell);
        }
        int buyQty = buy.getQuantity();
        int sellQty = sell.getQuantity();
        int executedQty = Math.min(buyQty, sellQty);
        BigDecimal price = calculateExecutedPrice(buy,sell);
        buys.deplete(executedQty);
        sells.deplete(executedQty);
        Execution buyExec = new ExecutionImpl(instrumentCode,price,executedQty,buy.getUser());
        Execution sellExec = new ExecutionImpl(instrumentCode,price,-executedQty,sell.getUser());
        listener.onExecution(buyExec);
        listener.onExecution(sellExec);
        updateAverage(price,executedQty);
    }

    private BigDecimal calculateExecutedPrice(Order order1, Order order2) {
        if (order1.getTimestamp() >= order2.getTimestamp()) {
            return order1.getPrice();
        }
        return order2.getPrice();
    }

    // ------------------------------------------------------------------------
    // Open Interest
    // ------------------------------------------------------------------------
    @Override
    public OpenInterest getOpenInterest() {
        List<Order> sellOrders;
        List<Order> buyOrders;
        synchronized (orderLock) {
            sellOrders = sells.asList();
            buyOrders = buys.asList();
        }
        return OpenInterestImpl.newInstance(instrumentCode, sellOrders, buyOrders);
    }

    // ------------------------------------------------------------------------
    // Average Execution
    // ------------------------------------------------------------------------
    @Override
    public BigDecimal getAverageExecutionPrice() {
        return averageExecution;
    }

    private void updateAverage(BigDecimal price, int executedQty) {
        double vol = price.doubleValue() * executedQty;
        double oldVol = averageExecution.doubleValue() * totalQuantity;
        totalQuantity += executedQty;
        averageExecution = BigDecimal.valueOf((oldVol + vol)/totalQuantity).setScale(4, RoundingMode.HALF_UP);
    }

    // ------------------------------------------------------------------------
    // Comparators for buy/sell orders
    // ------------------------------------------------------------------------
    private static Comparator<Order> createBuyComparitor() {
        return (a,b) -> {
            int compared = b.getPrice().compareTo(a.getPrice());
            return compared != 0 ? compared : Long.compare(b.getTimestamp(),a.getTimestamp());
        };
    }

    private static Comparator<Order> createSellComparitor() {
        return (a,b) -> {
            int compared = a.getPrice().compareTo(b.getPrice());
            return compared != 0 ? compared : Long.compare(b.getTimestamp(),a.getTimestamp());
        };
    }

    @Override
    public String toString() {
        return "InstrumentOrdersImpl{" +
                "instrumentCode='" + instrumentCode + '\'' +
                ", buys=" + buys +
                ", sells=" + sells +
                '}';
    }
}
