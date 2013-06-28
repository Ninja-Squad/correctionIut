package com.ninja_squad.formation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.fest.assertions.api.Assertions.*;

/**
 * Selenium test which checks that DbSetup can be found on Maven central and mvnrepository, and that no other
 * DbSetup exists
 * @author JB
 */
public class SeleniumTest {

    private WebDriver webDriver;

    @Before
    public void prepare() {
        webDriver = new FirefoxDriver();
        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @After
    public void cleanup() {
        webDriver.quit();
    }

    @Test
    public void testWithMavenDotOrg() {
        webDriver.get("http://search.maven.org/");
        WebElement searchBox = webDriver.findElement(By.id("query"));
        searchBox.sendKeys("dbsetup");
        WebElement searchButton = webDriver.findElement(By.id("queryButton"));
        searchButton.click();
        List<WebElement> rowsInBodyOfResultTable = webDriver.findElements(By.cssSelector("#resultTable tbody tr"));
        assertThat(rowsInBodyOfResultTable).hasSize(1);
        WebElement firstCell = rowsInBodyOfResultTable.get(0).findElement(By.tagName("td"));
        assertThat(firstCell.getText()).isEqualTo("com.ninja-squad");
    }

    @Test
    public void testWithMvnRepository() {
        webDriver.get("http://mvnrepository.com/");
        WebElement searchBox = webDriver.findElement(By.id("query"));
        searchBox.sendKeys("dbsetup");
        WebElement searchButton = webDriver.findElement(By.cssSelector("input.button[value='Search']"));
        searchButton.click();
        List<WebElement> resultParagraphs = webDriver.findElements(By.cssSelector("p.result"));
        assertThat(resultParagraphs).hasSize(1);
        assertThat(resultParagraphs.get(0).getText()).contains("com.ninja-squad");
    }
}
