Feature: Adding an Order

  Adding an order that matches should result in trades.

  Scenario: Adding a new Order
    Given I add an order to BUY 1000 IBM.L @ 135.00
    Then the open interest in IBM.L should be 1000
    And my executed orders should be 0

  Scenario: Adding a new Order matches an existing order
    Given I add an order to BUY 1000 IBM.L @ 135.00
    Then the open interest in IBM.L should be 1000
    And user2 adds an order to SELL 500 IBM.L @ 134.00
    Then my executed orders should be 500
    And the open interest in IBM.L should be 500

  Scenario: New Order matches multiple existing orders
    Given user2 adds an order to SELL 1000 IBM.L @ 135.00
    Given user3 adds an order to SELL 1000 IBM.L @ 135.00
    Given I add an order to BUY 3000 IBM.L @ 135.00
    Then my executed orders should be 2000
    And the open interest in IBM.L should be 1000

  Scenario: Order is executed at latest price
    Given I add an order to BUY 1000 IBM.L @ 135.00
    And user2 adds an order to SELL 1000 IBM.L @ 134.00
    And my last executed price should be 134.00

