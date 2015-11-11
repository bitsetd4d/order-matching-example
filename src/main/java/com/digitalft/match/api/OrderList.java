package com.digitalft.match.api;

import java.util.List;

/**
 * List of orders ordered by best price/latest time.
 */
public interface OrderList {

    List<Order> asList();

}
