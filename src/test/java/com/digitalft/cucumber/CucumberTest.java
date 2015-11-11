package com.digitalft.cucumber;

import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 * Execute Cucumber tests
 */

@RunWith(Cucumber.class)
@CucumberOptions(format={"pretty"}, snippets=SnippetType.CAMELCASE, dryRun=false)
public class CucumberTest {

}
