package com.digitalft.match;

import com.digitalft.match.api.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Test Scenario given in specification
 */
public class TestScenario {

    private OrderMatcher matcher;

    private int user1Vol;
    private int user2Vol;

    private List<LevelExpectation> levelExpectations = new ArrayList<>();

    @Before
    public void prepareMatcher() {
        matcher = OrderMatcher.newInstance();
        matcher.addListener(execution -> updateExecution(execution));
    }

    @Test
    public void testScenario() {
        sell(1000, 100.2, "User1");
        expectSellInterest(1000, 100.2);
        expectAverage(0);
        checkInterest();

        buy(1000, 100.2, "User2");
        expectExecution(-1000, 1000);
        expectAverage(100.2);
        checkInterest();

        buy(1000, 99.0, "User1");
        expectBuyInterest(1000, 99.0);
        expectExecution(-1000, 1000);
        expectAverage(100.2);
        checkInterest();

        buy(1000, 101.0, "User1");
        expectBuyInterest(1000, 101.0);
        expectBuyInterest(1000, 99.0);
        expectExecution(-1000, 1000);
        expectAverage(100.2);
        checkInterest();

        sell(500, 102.0, "User2");
        expectBuyInterest(1000, 101.0);
        expectBuyInterest(1000, 99.0);
        expectSellInterest(500, 102.0);
        expectExecution(-1000, 1000);
        expectAverage(100.2);
        checkInterest();

        buy(500, 103.0, "User1");
        expectBuyInterest(1000, 101.0);
        expectBuyInterest(1000, 99.0);
        expectExecution(-500, 500);
        expectAverage(101.1333);
        checkInterest();

        sell(1000, 98.0, "User2");
        expectBuyInterest(1000, 99.0);
        expectExecution(500, -500);
        expectAverage(99.8800);
        checkInterest();
    }

    private void updateExecution(Execution execution) {
        System.out.println("Execution: " + execution);
        if (execution.getUser().equals("User1")) {
            user1Vol += execution.getQuantity();
        } else if (execution.getUser().equals("User2")) {
            user2Vol += execution.getQuantity();
        } else {
            Assert.fail("Unknown user " + execution);
        }
    }

    private void buy(int quantity, double price, String user) {
        clearInterest();
        Order order = Order.newBuy("VOD.L", BigDecimal.valueOf(price), quantity, user);
        System.out.println("\nBUY: "+order);
        matcher.submit(order);
    }

    private void sell(int quantity, double price, String user) {
        clearInterest();
        Order order = Order.newSell("VOD.L", BigDecimal.valueOf(price), quantity, user);
        System.out.println("\nSELL: " + order);
        matcher.submit(order);
    }

    private void expectExecution(int user1,int user2) {
        Assert.assertEquals("User1 Volume", user1, user1Vol);
        Assert.assertEquals("User2 Volume", user2, user2Vol);
    }

    private void expectAverage(double average) {
        BigDecimal averageExecutionPrice = matcher.getOrders("VOD.L").getAverageExecutionPrice();
        BigDecimal expected = BigDecimal.valueOf(average);
        System.out.println("Expecting "+expected+" - check "+averageExecutionPrice);
        Assert.assertTrue(expected.compareTo(averageExecutionPrice) == 0);
    }

    // ------------------------------------------------------------------------
    // Setup/check expectation of open interest
    // ------------------------------------------------------------------------
    private void clearInterest() {
        levelExpectations.clear();
    }

    private void checkInterest() {
        OpenInterest openInterest = matcher.getOrders("VOD.L").getOpenInterest();
        List<PriceLevel> levels = openInterest.getLevels();
        Assert.assertEquals(levelExpectations.size(),levels.size());
        for (int i=0; i<levelExpectations.size(); i++) {
            PriceLevel level = levels.get(i);
            LevelExpectation expectation = levelExpectations.get(i);
            expectation.check(level);
        }
    }

    private void expectSellInterest(int quantity, double price) {
        levelExpectations.add(new LevelExpectation(-quantity,price));
    }

    private void expectBuyInterest(int quantity, double price) {
        levelExpectations.add(new LevelExpectation(quantity,price));
    }

    private static class LevelExpectation {

        int quantity;
        BigDecimal price;

        public LevelExpectation(int quantity, double price) {
            this.quantity = quantity;
            this.price = BigDecimal.valueOf(price);
        }

        void check(PriceLevel level) {
            System.out.println("Checking "+this+" vs "+level);
            Assert.assertEquals(quantity,level.getQuantity());
            Assert.assertTrue(price.compareTo(level.getPrice()) == 0);
        }

        @Override
        public String toString() {
            return "LevelExpectation{" +
                    "quantity=" + quantity +
                    ", price=" + price +
                    '}';
        }
    }

}
