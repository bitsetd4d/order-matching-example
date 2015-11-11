package com.digitalft.cucumber;

import com.digitalft.match.api.*;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.junit.Assert;

import java.math.BigDecimal;
import java.util.List;

/**
 * Steps to support adding_order.feature
 */
public class AddingOrderSteps {

    private OrderMatcher matcher = OrderMatcher.newInstance();
    {
        matcher.addListener(execution -> recordExecution(execution));
    }

    private Execution myLastExecution;
    private int myTotalExecution;

    private void recordExecution(Execution execution) {
        System.out.println("<-- Execution: "+execution);
        if (execution.getUser().equals("I")) {
            myLastExecution = execution;
            myTotalExecution += execution.getQuantity();
        }
    }

    @Given("(.+) adds? an order to (BUY|SELL) (\\d+) (.+) @ (\\d+\\.\\d+)$")
    public void addAnOrder(String who, String buyOrSell, int quantity, String ticker, BigDecimal price) throws Throwable {
        boolean buy = buyOrSell.equals("BUY");
        Order order;
        if (buy) {
            order = Order.newBuy(ticker,price,quantity,who);
        } else {
            order = Order.newSell(ticker, price, quantity, who);
        }
        matcher.submit(order);
        System.out.println("--> Submit " + order + " to " + matcher);
    }

    @Then("^the open interest in (.+) should be (-?\\d+)$")
    public void checkOpenInterest(String code,int expected) throws Throwable {
        OpenInterest openInterest = matcher.getOrders(code).getOpenInterest();
        List<PriceLevel> levels = openInterest.getLevels();
        int qty = levels.stream().mapToInt(level -> level.getQuantity()).sum();
        Assert.assertEquals(expected, qty);
    }


    @Then("^my executed orders should be (-?\\d+)$")
    public void checkExecutedOrderVolume(int expected) throws Throwable {
        Assert.assertEquals(expected,myTotalExecution);
    }

    @Given("^my last executed price should be (\\d+\\.\\d+)$")
    public void checkLastExecutedPrice(BigDecimal expected) throws Throwable {
        if (myLastExecution == null) {
            Assert.assertEquals(expected,BigDecimal.ZERO);
            return;
        }
        Assert.assertEquals(expected,myLastExecution.getPrice());
    }

}
