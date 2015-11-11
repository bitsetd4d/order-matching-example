package com.digitalft.match.api;

/**
 * Listen for Executions by Order Matcher
 */
public interface OrderMatcherListener {

    void onExecution(Execution execution);

}
