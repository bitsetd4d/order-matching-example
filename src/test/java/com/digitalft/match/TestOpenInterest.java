package com.digitalft.match;

import com.digitalft.match.api.OpenInterest;
import com.digitalft.match.api.Order;
import com.digitalft.match.api.OrderMatcher;
import com.digitalft.match.api.PriceLevel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

/**
 * Test Open Interest Requirement
 */
public class TestOpenInterest {

    private OrderMatcher matcher;

    @Before
    public void prepareMatcher() {
        matcher = OrderMatcher.newInstance();
    }

    @Test
    public void testOpenInterest() {
        Order buy =  Order.newBuy("VOD.L",   BigDecimal.valueOf(99), 1000, "USER1");
        Order sell1 = Order.newSell("VOD.L", BigDecimal.valueOf(100), 500, "USER2");
        Order sell2 = Order.newSell("BT.L",  BigDecimal.valueOf(99), 2500, "USER3");
        Order sell3 = Order.newSell("BT.L",  BigDecimal.valueOf(99),  500, "USER4");

        matcher.submit(buy);
        matcher.submit(sell1);
        matcher.submit(sell2);
        matcher.submit(sell3);

        OpenInterest vodInterest = matcher.getOrders("VOD.L").getOpenInterest();
        Assert.assertEquals("VOD.L", vodInterest.getInstrumentCode());

        List<PriceLevel> levels = vodInterest.getLevels();
        Assert.assertEquals(2,levels.size());
        Assert.assertEquals(BigDecimal.valueOf(99), levels.get(0).getPrice());
        Assert.assertEquals(1000, levels.get(0).getQuantity());
        Assert.assertEquals(BigDecimal.valueOf(100), levels.get(1).getPrice());
        Assert.assertEquals(-500, levels.get(1).getQuantity());

        OpenInterest btInterest = matcher.getOrders("BT.L").getOpenInterest();
        Assert.assertEquals("BT.L", btInterest.getInstrumentCode());
        List<PriceLevel> btLevels = btInterest.getLevels();
        Assert.assertEquals(1,btLevels.size());
        Assert.assertEquals(BigDecimal.valueOf(99),btLevels.get(0).getPrice());
        Assert.assertEquals(-3000,btLevels.get(0).getQuantity());
    }

}
