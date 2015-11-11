package com.digitalft.match.api;

import com.digitalft.match.internal.OrderMatcherImpl;

/**
 * Provides Order Matching functionality.
 */
public interface OrderMatcher {

    static OrderMatcher newInstance() {
        return new OrderMatcherImpl();
    }

    void addListener(OrderMatcherListener listener);
    void removeListener(OrderMatcherListener listener);

    void submit(Order order);

    InstrumentOrders getOrders(String ric);

}
