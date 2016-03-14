package com.noptech.stira.service;

import com.noptech.stira.security.AuthoritiesConstants;
import com.noptech.stira.web.rest.dto.StormStatusDTO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Transactional
@Secured(AuthoritiesConstants.USER)
@PropertySource("classpath:credentials.properties")
public class StormService {

    private final Logger log = LoggerFactory.getLogger(StormService.class);

    @Value("${storm.user}")
    private String stormUser;
    @Value("${storm.password}")
    private String stormPass;


    public StormStatusDTO getStatus(long ticketIdParam) {
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


    @Scheduled(fixedRate = 5000)
    public void runStormJob() {
        // First check storm for updated issues, add to queue

        // Update storm queue count

        // Process oldest updated issue from queue
        log.debug("Running storm job");
    }
}
