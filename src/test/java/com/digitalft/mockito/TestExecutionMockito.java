package com.digitalft.mockito;

import com.digitalft.match.api.Order;
import com.digitalft.match.api.OrderMatcher;
import com.digitalft.match.api.OrderMatcherListener;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

/**
 * Test with mocks
 */
public class TestExecutionMockito {

    private OrderMatcher matcher;
    private OrderMatcherListener listener;

    @Before
    public void setupMatcher() {
        matcher = OrderMatcher.newInstance();
        listener = mock(OrderMatcherListener.class);
        matcher.addListener(listener);
    }

    @Test
    public void checkExecution() {
        /* Wouldn't normally mock a value object but to show example... */
        Order order = mock(Order.class);
        when(order.getInstrumentCode()).thenReturn("BT.L");
        when(order.getQuantity()).thenReturn(1000);
        when(order.getPrice()).thenReturn(BigDecimal.valueOf(100.0));
        when(order.getTimestamp()).thenReturn(System.nanoTime());
        when(order.getUser()).thenReturn("mock1");
        when(order.isBuyNotSell()).thenReturn(true);
        matcher.submit(order);
        verify(listener, never()).onExecution(null);

        Order order2 = mock(Order.class);
        when(order2.getInstrumentCode()).thenReturn("BT.L");
        when(order2.getQuantity()).thenReturn(1000);
        when(order2.getPrice()).thenReturn(BigDecimal.valueOf(100.0));
        when(order2.getTimestamp()).thenReturn(System.nanoTime());
        when(order2.getUser()).thenReturn("mock2");
        when(order2.isBuyNotSell()).thenReturn(false);
        matcher.submit(order2);
        verify(listener, times(2)).onExecution(any());
    }

    @Test
    public void checkFieldsAccessed() {
        Order buy =  spy(Order.newBuy("VOD.L", BigDecimal.valueOf(100), 1000, "USER1"));
        Order sell = spy(Order.newSell("VOD.L", BigDecimal.valueOf(99), 1000, "USER2"));
        matcher.submit(buy);
        matcher.submit(sell);
        verify(buy,atLeast(1)).getInstrumentCode();
        verify(sell,atLeast(1)).getInstrumentCode();
    }

}
