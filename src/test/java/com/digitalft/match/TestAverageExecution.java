package com.digitalft.match;

import com.digitalft.match.api.Order;
import com.digitalft.match.api.OrderMatcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Average execution price
 */
public class TestAverageExecution {

    private OrderMatcher matcher;
    private int executions;

    @Before
    public void prepareMatcher() {
        matcher = OrderMatcher.newInstance();
        executions = 0;
    }

    @Test
    public void testSimpleAverage() {
        Order buy =  Order.newBuy("VOD.L",   BigDecimal.valueOf(100), 1000, "USER1");
        Order sell1 = Order.newSell("VOD.L", BigDecimal.valueOf(97.0), 100, "USER2");
        Order sell2 = Order.newSell("VOD.L", BigDecimal.valueOf(98.0), 100, "USER3");
        Order sell3 = Order.newSell("VOD.L", BigDecimal.valueOf(99.0), 100, "USER4");

        matcher.addListener(execution -> executions++);
        matcher.submit(buy);
        matcher.submit(sell1);
        matcher.submit(sell2);
        matcher.submit(sell3);

        Assert.assertEquals(6, executions);
        BigDecimal average = matcher.getOrders("VOD.L").getAverageExecutionPrice();
        Assert.assertEquals(as4DP(BigDecimal.valueOf(98.0)),average);
    }

    @Test
    public void testAverageDifferentQuantites() {
        Order buy =  Order.newBuy("VOD.L",   BigDecimal.valueOf(110), 1000, "USER1");
        Order sell1 = Order.newSell("VOD.L", BigDecimal.valueOf(100), 100, "USER2");
        Order sell2 = Order.newSell("VOD.L", BigDecimal.valueOf(50.0), 90, "USER3");
        Order sell3 = Order.newSell("VOD.L", BigDecimal.valueOf(10.0), 80, "USER4");

        int total = 100 + 90 + 80;
        BigDecimal expected = as4DP(BigDecimal.valueOf((100.0*100 + 50.0*90 + 10.0*80) / total));

        matcher.addListener(execution -> executions++);
        matcher.submit(buy);
        matcher.submit(sell1);
        matcher.submit(sell2);
        matcher.submit(sell3);

        Assert.assertEquals(6, executions);
        BigDecimal average = matcher.getOrders("VOD.L").getAverageExecutionPrice();
        Assert.assertEquals(expected,average);
    }

    private BigDecimal as4DP(BigDecimal bd) {
        return bd.setScale(4, RoundingMode.HALF_UP);
    }

}
