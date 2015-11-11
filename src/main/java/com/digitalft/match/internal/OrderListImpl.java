package com.digitalft.match.internal;

import com.digitalft.match.api.Order;
import com.digitalft.match.api.OrderList;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

/**
 * List of orders
 */
public class OrderListImpl implements OrderList {

    private final PriorityQueue<Order> queue;

    public OrderListImpl(Comparator<Order> comparator) {
        queue = new PriorityQueue<>(comparator);
    }

    @Override
    public List<Order> asList() {
        return queue.stream().collect(Collectors.toList());
    }

    void add(Order order) {
        queue.add(order);
    }

    public Order getBest() {
        return queue.peek();
    }

    public void deplete(int executedQty) {
        Order best = queue.remove();
        int remaining = best.getQuantity() - executedQty;
        if (remaining > 0) {
            Order replacementOrder = best.withNewQuantity(remaining);
            add(replacementOrder);
        }
    }

    @Override
    public String toString() {
        return "OrderListImpl{" +
                "queue=" + queue +
                '}';
    }

}
