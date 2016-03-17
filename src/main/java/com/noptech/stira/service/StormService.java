package com.noptech.stira.service;

import com.noptech.stira.domain.QueueSource;
import com.noptech.stira.domain.QueuedForUpdate;
import com.noptech.stira.domain.Ticket;
import com.noptech.stira.domain.enumeration.TicketSource;
import com.noptech.stira.repository.QueueSourceRepository;
import com.noptech.stira.repository.TicketRepository;
import com.noptech.stira.web.rest.dto.StormStatusDTO;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@PropertySource("classpath:credentials.properties")
public class StormService {

    private final Logger log = LoggerFactory.getLogger(StormService.class);

    @Value("${storm.user}")
    private String stormUser;
    @Value("${storm.password}")
    private String stormPass;

    @Inject
    private QueueSourceRepository queueSourceRepository;

    @Inject
    private QueuedForUpdateService queuedForUpdateService;

    @Inject
    private TicketRepository ticketRepository;

    private WebDriver driver;
    QueueSource stormSource;

    private void setupDriver() {
        if (driver != null) {
            driver.quit();
        }
        driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    private void login() {
        setupDriver();
        driver.get("https://customer.tdchosting.com");
        WebElement form = null;
        try {
            form = driver.findElement(By.cssSelector(".loginBox input[type=submit]"));
        } catch (Exception e) {}

        if (form != null) {
            WebElement usernameField = driver.findElement(By.id("email"));
            WebElement passwordField = driver.findElement(By.id("password"));
            usernameField.sendKeys(stormUser);
            passwordField.sendKeys(stormPass);

            form.submit();
        }
        // Find element - ensure page is loaded
        driver.findElement(By.cssSelector("a[title=Tickets]"));
    }


    public StormStatusDTO getStatus(long ticketIdParam) {
        StormStatusDTO stormStatus = new StormStatusDTO();
        stormStatus.setId(ticketIdParam);

        login();

        driver.navigate().to("https://customer.tdchosting.com/tickets/tickets-details/?id=" + ticketIdParam);

        WebElement priorityElem = driver.findElement(By.cssSelector("div.tick-info-table:nth-child(1) > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(2)"));
        short priority = Short.parseShort(priorityElem.getText().substring(4));
        stormStatus.setPriority(priority);

        WebElement statusElem = driver.findElement(By.cssSelector("div.tick-info-table:nth-child(1) > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(5) > td:nth-child(2)"));
        stormStatus.setStatus(statusElem.getText());
        driver.quit();
        return stormStatus;
    }


    @Scheduled(fixedDelay = 30000)
    public void runStormJob() throws Exception {
        log.debug("Running storm job");
        login();
        if (stormSource == null) {
            stormSource = queueSourceRepository.findByTicketSource(TicketSource.STORM);
        }
        // First check storm for updated issues, add to queue
        try {
            Pair<LocalDateTime, List<Ticket>> updatedTicketsAndMaxDate = getUpdatedTickets();
            List<Ticket> updatedTickets = updatedTicketsAndMaxDate.getRight();
            LocalDateTime maxDate = updatedTicketsAndMaxDate.getLeft();

            queuedForUpdateService.addToQueue(updatedTickets);

            if (stormSource.getLastAddedTicket() == null || stormSource.getLastAddedTicket().isBefore(maxDate)) {
                stormSource.setLastAddedTicket(maxDate);
                stormSource = queueSourceRepository.save(stormSource);
            }

            // TODO -  Update storm queue count (add column to entity)

            // Process oldest updated issue from queue
            QueuedForUpdate next = queuedForUpdateService.getNext(TicketSource.STORM);
            log.debug("NEXT: " + (next != null ? next.toString() : null));
            if (next != null) {
                processTicket(next);
                queuedForUpdateService.delete(next.getId());
            }
        } finally {
            driver.quit();
        }
    }

    private void processTicket(QueuedForUpdate next) {
        login();
        driver.navigate().to("https://customer.tdchosting.com/tickets/tickets-details/?id=" + next.getTicketKey());
        Ticket t = getStormTicketInfo(next.getTicketKey());


        ticketRepository.save(t);
    }

    private Ticket getStormTicketInfo(String ticketKey) {
        log.debug("Getting storm ticket info for: https://customer.tdchosting.com/tickets/tickets-details/?id=" + ticketKey);
        Ticket t = new Ticket();
        t.setStormKey(Long.valueOf(ticketKey));

        /*
        WebElement titleElem = driver.findElement(By.cssSelector(".ticket-details-info > p:nth-child(11)"));
        t.setStormTitle(titleElem.getText().trim());
        */

        WebElement lastUpdatedElem = driver.findElement(By.cssSelector("div.tick-info-table:nth-child(1) > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(4) > td:nth-child(2)"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        t.setStormLastUpdated(LocalDateTime.parse(lastUpdatedElem.getText(), formatter));
        /*
        WebElement statusElem = driver.findElement(By.cssSelector("div.tick-info-table:nth-child(1) > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(5) > td:nth-child(2)"));
        t.setStatus(statusElem.getText());
        */
        return t;
    }

    private Pair<LocalDateTime, List<Ticket>> getUpdatedTickets() throws Exception {
        if (stormSource == null) {
            throw new Exception("No storm source found in database");
        }

        LocalDateTime cutoffDateTime = stormSource.getLastAddedTicket() == null
            ? LocalDateTime.now().minusDays(30) : stormSource.getLastAddedTicket();

        return getTicketsUpdatedAfter(cutoffDateTime);
    }

    private List<Ticket> getAllTicketsOnDashboard() throws Exception {
        List<Ticket> tickets = new ArrayList<Ticket>();
        List<WebElement> elements = driver.findElements(By.tagName("tr"));
        for (WebElement elem : elements) {
            Ticket ticket = new Ticket();
            List<WebElement> columns = elem.findElements(By.tagName("td"));
            for (WebElement col : columns) {
                switch(col.getAttribute("class")) {
                    case "col1": // TicketKey
                        ticket.setStormKey(Long.valueOf(col.getText()));
                        break;
                    case "type-td": // priority
                        break;
                    case "updated-td": // last updated
                        String timestamp = col.getText();
                        LocalDateTime dateTime = null;
                        if (timestamp.contains(":")) {
                            if (timestamp.length() == 5) {
                                dateTime = LocalDateTime.parse(LocalDate.now().toString() + "T" + timestamp + ":00");
                            } else {
                                LocalDate date = getDateFromDayOfWeek(timestamp);
                                dateTime = LocalDateTime.parse(date.toString() + "T" + timestamp.substring(4,9) + ":00");
                            }
                        }
                        ticket.setStormLastUpdated(dateTime);
                        break;
                    case "problem-area": // title
                        break;
                    default:
                        break;
                }
            }
            if (ticket.getStormLastUpdated() != null) {
                tickets.add(ticket);
            }
        }
        return tickets;
    }

    private LocalDate getDateFromDayOfWeek(String timestamp) throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        int currentDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        int timestampDayOfWeek = getDayOfWeekFromString(timestamp.substring(0, 3), cal);
        if (currentDayOfWeek <= timestampDayOfWeek) {
            currentDayOfWeek += 7;
        }
        int daysDiff = currentDayOfWeek - timestampDayOfWeek;

        return LocalDate.now().minusDays(daysDiff);
    }

    private int getDayOfWeekFromString(String dow, Calendar cal) throws Exception {
        int firstDayOfWeek = cal.getFirstDayOfWeek();
        switch(dow) {
            case "Mon":
                return firstDayOfWeek;
            case "Tue":
                return firstDayOfWeek + 1;
            case "Wed":
                return firstDayOfWeek + 2;
            case "Thu":
                return firstDayOfWeek + 3;
            case "Fri":
                return firstDayOfWeek + 4;
            case "Sat":
                return firstDayOfWeek + 5;
            case "Sun":
                return firstDayOfWeek + 6;
            default:
                throw new Exception("Invalid day of week: " + dow);
        }
    }

    private Pair<LocalDateTime, List<Ticket>> getTicketsUpdatedAfter(LocalDateTime cutoffDateTime) throws Exception {
        List<Ticket> tickets = getAllTicketsOnDashboard();
        LocalDateTime maxDate = cutoffDateTime;

        List<Ticket> toBeRemoved = new ArrayList<Ticket>();
        for (Ticket t : tickets) {
            if (t.getStormLastUpdated().isBefore(cutoffDateTime)) {
                toBeRemoved.add(t);
            } else {
                if (t.getStormLastUpdated().isAfter(maxDate)) {
                    maxDate = t.getStormLastUpdated();
                }
            }
        }
        tickets.removeAll(toBeRemoved);
        return new ImmutablePair<>(maxDate, tickets);
        /*
        for (WebElement elem : elements) {
            if (elem.get.getText().contains(":")) {
                LocalDateTime updated = LocalDateTime.parse(elem.getText());
                if (updated.isAfter(cutoffDateTime)) {
                    Ticket t = new Ticket();
                    t.setStormKey();
                }
            }
        }
        */
    }
}
