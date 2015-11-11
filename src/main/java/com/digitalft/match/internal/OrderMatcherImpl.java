package com.digitalft.match.internal;

import com.digitalft.match.api.InstrumentOrders;
import com.digitalft.match.api.Order;
import com.digitalft.match.api.OrderMatcher;
import com.digitalft.match.api.OrderMatcherListener;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implements Order Matching.
 */
public class OrderMatcherImpl implements OrderMatcher {

    private List<OrderMatcherListener> listeners = new CopyOnWriteArrayList<>();
    private OrderMatcherListener listenerDelegate = execution -> listeners.forEach(l -> l.onExecution(execution));

    private ConcurrentHashMap<String,InstrumentOrdersImpl> instrumentOrders = new ConcurrentHashMap<>();

    @Override
    public void addListener(OrderMatcherListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(OrderMatcherListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void submit(Order order) {
        InstrumentOrdersImpl instrumentOrders = getInstrumentOrders(order.getInstrumentCode());
        instrumentOrders.submit(order,listenerDelegate);
    }

    @Override
    public InstrumentOrders getOrders(String ric) {
        return getInstrumentOrders(ric);
    }

    private InstrumentOrdersImpl getInstrumentOrders(String ric) {
        return instrumentOrders.computeIfAbsent(ric, key -> new InstrumentOrdersImpl(ric));
    }

}
