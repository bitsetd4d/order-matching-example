package com.digitalft.match;

import com.digitalft.match.api.InstrumentOrders;
import com.digitalft.match.api.Order;
import com.digitalft.match.api.OrderMatcher;
import com.digitalft.match.internal.OrderImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

/**
 * Test the structural code.
 */
public class TestPlumbing {

    private OrderMatcher matcher;

    @Before
    public void prepareMatcher() {
        matcher = OrderMatcher.newInstance();
    }

    @Test
    public void testAddingOrders() {
        Order order = Order.newBuy("VOD.L", BigDecimal.valueOf(100.2), 1000, "TEST");
        Order order2 = Order.newBuy("VOD.L", BigDecimal.valueOf(100.3), 1000, "TEST");
        Order order3 = Order.newSell("VOD.L", BigDecimal.valueOf(100.4), 1000, "TEST");
        Order order4 = Order.newSell("BT.L", BigDecimal.valueOf(100.4), 1000, "TEST");

        InstrumentOrders orders = matcher.getOrders("VOD.L");
        Assert.assertEquals(0, orders.getBuyOrders().asList().size());
        Assert.assertEquals(0, orders.getSellOrders().asList().size());

        matcher.submit(order);
        Assert.assertEquals(1, orders.getBuyOrders().asList().size());
        Assert.assertEquals(0, orders.getSellOrders().asList().size());

        matcher.submit(order2);
        Assert.assertEquals(2, orders.getBuyOrders().asList().size());
        Assert.assertEquals(0, orders.getSellOrders().asList().size());

        matcher.submit(order3);
        Assert.assertEquals(2, orders.getBuyOrders().asList().size());
        Assert.assertEquals(1, orders.getSellOrders().asList().size());

        matcher.submit(order4);
        Assert.assertEquals(2, orders.getBuyOrders().asList().size());
        Assert.assertEquals(1, orders.getSellOrders().asList().size());
    }

    @Test
    public void testListImmutables() {
        Order order = Order.newBuy("VOD.L", BigDecimal.valueOf(100.2), 1000, "TEST");
        InstrumentOrders orders = matcher.getOrders("VOD.L");
        List<Order> orderList = orders.getBuyOrders().asList();
        Assert.assertTrue(orders.getBuyOrders().asList().isEmpty());
        orderList.add(order);
        Assert.assertTrue("Real list should not have been affected",orders.getBuyOrders().asList().isEmpty());
    }

    @Test
    public void testBestBuy() {
        Order good   = Order.newBuy("VOD.L", BigDecimal.valueOf(100.1), 1000, "GOOD");
        Order better = Order.newBuy("VOD.L", BigDecimal.valueOf(100.2), 1000, "BETTER");
        Order worst  = Order.newBuy("VOD.L", BigDecimal.valueOf(99),    1000, "WORST");
        Order best   = Order.newBuy("VOD.L", BigDecimal.valueOf(100.3), 1000, "BEST");

        InstrumentOrders orders = matcher.getOrders("VOD.L");

        matcher.submit(good);
        List<Order> orderList = orders.getBuyOrders().asList();
        Assert.assertEquals(1, orderList.size());
        Assert.assertTrue(orderList.get(0).getUser().equals("GOOD"));

        matcher.submit(better);
        orderList = orders.getBuyOrders().asList();
        Assert.assertEquals(2, orderList.size());
        Assert.assertTrue(orderList.get(0).getUser().equals("BETTER"));

        matcher.submit(worst);
        orderList = orders.getBuyOrders().asList();
        Assert.assertEquals(3, orderList.size());
        Assert.assertTrue(orderList.get(0).getUser().equals("BETTER"));

        matcher.submit(best);
        orderList = orders.getBuyOrders().asList();
        Assert.assertEquals(4, orderList.size());
        Assert.assertTrue(orderList.get(0).getUser().equals("BEST"));
    }


    @Test
    public void testBestSell() {
        Order good   = Order.newSell("VOD.L", BigDecimal.valueOf(100.5), 1000, "GOOD");
        Order better = Order.newSell("VOD.L", BigDecimal.valueOf(100.4), 1000, "BETTER");
        Order worst  = Order.newSell("VOD.L", BigDecimal.valueOf(200), 1000, "WORST");
        Order best   = Order.newSell("VOD.L", BigDecimal.valueOf(100.3), 1000, "BEST");

        InstrumentOrders orders = matcher.getOrders("VOD.L");

        matcher.submit(good);
        List<Order> orderList = orders.getSellOrders().asList();
        Assert.assertEquals(1, orderList.size());
        Assert.assertTrue(orderList.get(0).getUser().equals("GOOD"));

        matcher.submit(better);
        orderList = orders.getSellOrders().asList();
        Assert.assertEquals(2, orderList.size());
        Assert.assertTrue(orderList.get(0).getUser().equals("BETTER"));

        matcher.submit(worst);
        orderList = orders.getSellOrders().asList();
        Assert.assertEquals(3, orderList.size());
        Assert.assertTrue(orderList.get(0).getUser().equals("BETTER"));

        matcher.submit(best);
        orderList = orders.getSellOrders().asList();
        Assert.assertEquals(4, orderList.size());
        Assert.assertTrue(orderList.get(0).getUser().equals("BEST"));
    }

    @Test
    public void testOlderOrdersPrioritisedBuy() {
        Order oldOrder  = new OrderImpl("VOD.L", true, BigDecimal.valueOf(100.5), 1000, "ORDER1", 100000);
        Order newOrder  = new OrderImpl("VOD.L", true, BigDecimal.valueOf(100.5), 1000, "ORDER1", 200000);
        InstrumentOrders orders = matcher.getOrders("VOD.L");

        matcher.submit(oldOrder);
        List<Order> orderList = orders.getBuyOrders().asList();
        Assert.assertEquals(1, orderList.size());
        Assert.assertTrue(orderList.get(0).getUser().equals("ORDER1"));

        matcher.submit(newOrder);
        orderList = orders.getBuyOrders().asList();
        Assert.assertEquals(2, orderList.size());
        Assert.assertTrue(orderList.get(0).getUser().equals("ORDER1"));

    }

    @Test
    public void testOlderOrdersPrioritisedSell() {
        Order oldOrder  = new OrderImpl("VOD.L", false, BigDecimal.valueOf(100.5), 1000, "ORDER1", 100000);
        Order newOrder  = new OrderImpl("VOD.L", false, BigDecimal.valueOf(100.5), 1000, "ORDER1", 200000);
        InstrumentOrders orders = matcher.getOrders("VOD.L");

        matcher.submit(oldOrder);
        List<Order> orderList = orders.getSellOrders().asList();
        Assert.assertEquals(1, orderList.size());
        Assert.assertTrue(orderList.get(0).getUser().equals("ORDER1"));

        matcher.submit(newOrder);
        orderList = orders.getSellOrders().asList();
        Assert.assertEquals(2, orderList.size());
        Assert.assertTrue(orderList.get(0).getUser().equals("ORDER1"));

    }


}
