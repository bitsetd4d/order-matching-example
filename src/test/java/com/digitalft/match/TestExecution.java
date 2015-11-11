package com.digitalft.match;

import com.digitalft.match.api.Execution;
import com.digitalft.match.api.Order;
import com.digitalft.match.api.OrderMatcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Test logic of executing orders
 */
public class TestExecution {

    private OrderMatcher matcher;

    @Before
    public void prepareMatcher() {
        matcher = OrderMatcher.newInstance();
    }

    @Test
    public void testNoMatch() {
        matcher.addListener(execution -> Assert.fail("Should not have caused execution"));
        Order buy = Order.newBuy("BT.L", BigDecimal.valueOf(101), 1000, "TEST");
        Order sell = Order.newSell("VOD.L", BigDecimal.valueOf(99), 1000, "TEST");
        matcher.submit(buy);
        matcher.submit(sell);
    }

    @Test
    public void testSimpleSellMatchesBuy() {
        final List<Execution> executed = new ArrayList<>();
        matcher.addListener(execution -> executed.add(execution));

        Order buy =  Order.newBuy("VOD.L", BigDecimal.valueOf(100), 1000, "USER1");
        Order sell = Order.newSell("VOD.L", BigDecimal.valueOf(99), 1000, "USER2");

        matcher.submit(buy);
        Assert.assertTrue(executed.isEmpty());

        matcher.submit(sell);

        Assert.assertEquals(2, executed.size());
        Execution exec1 = executed.stream().filter(e -> e.getUser().equals("USER1")).findAny().get();
        Execution exec2 = executed.stream().filter(e -> e.getUser().equals("USER2")).findAny().get();
        Assert.assertEquals(1000,exec1.getQuantity());
        Assert.assertEquals(BigDecimal.valueOf(99), exec1.getPrice());
        Assert.assertEquals(-1000, exec2.getQuantity());
        Assert.assertEquals(BigDecimal.valueOf(99), exec2.getPrice());
    }

    @Test
    public void testSimpleBuyMatchesSell() {
        final List<Execution> executed = new ArrayList<>();
        matcher.addListener(execution -> executed.add(execution));

        Order sell = Order.newSell("VOD.L", BigDecimal.valueOf(99), 1000, "USER1");
        Order buy =  Order.newBuy("VOD.L", BigDecimal.valueOf(100), 1000, "USER2");

        matcher.submit(sell);
        Assert.assertTrue(executed.isEmpty());

        matcher.submit(buy);

        Assert.assertEquals(2, executed.size());
        Execution exec1 = executed.stream().filter(e -> e.getUser().equals("USER1")).findAny().get();
        Execution exec2 = executed.stream().filter(e -> e.getUser().equals("USER2")).findAny().get();
        Assert.assertEquals(-1000,exec1.getQuantity());
        Assert.assertEquals(BigDecimal.valueOf(100), exec1.getPrice());
        Assert.assertEquals(1000,exec2.getQuantity());
        Assert.assertEquals(BigDecimal.valueOf(100), exec2.getPrice());
    }

    @Test
    public void testPartialExecution() {
        final List<Execution> executed = new ArrayList<>();
        matcher.addListener(execution -> executed.add(execution));

        Order buy =  Order.newBuy("VOD.L", BigDecimal.valueOf(100),   1000, "USER1");
        Order sell = Order.newSell("VOD.L", BigDecimal.valueOf(99), 500, "USER2");
        Order sell2 = Order.newSell("VOD.L", BigDecimal.valueOf(99.5), 500, "USER3");

        matcher.submit(buy);
        Assert.assertTrue(executed.isEmpty());

        matcher.submit(sell);

        Assert.assertEquals(2, executed.size());
        Execution exec1 = executed.stream().filter(e -> e.getUser().equals("USER1")).findAny().get();
        Execution exec2 = executed.stream().filter(e -> e.getUser().equals("USER2")).findAny().get();

        Assert.assertEquals(500, exec1.getQuantity());
        Assert.assertEquals(BigDecimal.valueOf(99), exec1.getPrice());

        Assert.assertEquals(-500, exec2.getQuantity());
        Assert.assertEquals(BigDecimal.valueOf(99), exec2.getPrice());

        executed.clear();
        matcher.submit(sell2);
        Assert.assertEquals(2, executed.size());
        Execution exec3 = executed.stream().filter(e -> e.getUser().equals("USER1")).findAny().get();
        Execution exec4 = executed.stream().filter(e -> e.getUser().equals("USER3")).findAny().get();

        Assert.assertEquals(500, exec3.getQuantity());
        Assert.assertEquals(BigDecimal.valueOf(99.5), exec3.getPrice());

        Assert.assertEquals(-500, exec4.getQuantity());
        Assert.assertEquals(BigDecimal.valueOf(99.5), exec4.getPrice());

    }

}
