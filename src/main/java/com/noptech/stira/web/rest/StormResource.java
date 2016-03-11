package com.noptech.stira.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.noptech.stira.web.rest.dto.StormStatusDTO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * Controller for test purposes
 */
@RestController
@RequestMapping("/api")
@PropertySource("classpath:credentials.properties")
public class StormResource {

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    @Value("${stormcredentials.user}")
    private String stormUser;
    @Value("${stormcredentials.password}")
    private String stormPass;

    @RequestMapping(value = "/storm/{ticketIdParam:.+}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public StormStatusDTO getStatus(@PathVariable Long ticketIdParam) {
        StormStatusDTO stormStatus = new StormStatusDTO();
        stormStatus.setId(ticketIdParam);

        WebDriver driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get("https://customer.tdchosting.com");

        WebElement usernameField = driver.findElement(By.id("email"));
        WebElement passwordField = driver.findElement(By.id("password"));
        usernameField.sendKeys(stormUser);
        passwordField.sendKeys(stormPass);

        WebElement form = driver.findElement(By.cssSelector(".loginBox input[type=submit]"));
        form.submit();

        // Find element - ensure page is loaded
        driver.findElement(By.cssSelector("a[title=Tickets]"));

        driver.navigate().to("https://customer.tdchosting.com/tickets/tickets-details/?id=" + ticketIdParam);

        WebElement priorityElem = driver.findElement(By.cssSelector("div.tick-info-table:nth-child(1) > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(2)"));
        short priority = Short.parseShort(priorityElem.getText().substring(4));
        stormStatus.setPriority(priority);

        WebElement statusElem = driver.findElement(By.cssSelector("div.tick-info-table:nth-child(1) > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(5) > td:nth-child(2)"));
        stormStatus.setStatus(statusElem.getText());
        driver.close();
        return stormStatus;
    }
}
