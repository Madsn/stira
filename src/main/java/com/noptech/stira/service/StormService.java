package com.noptech.stira.service;

import com.noptech.stira.domain.QueueSource;
import com.noptech.stira.domain.QueuedForUpdate;
import com.noptech.stira.domain.Ticket;
import com.noptech.stira.domain.enumeration.TicketSource;
import com.noptech.stira.domain.enumeration.TicketStatus;
import com.noptech.stira.repository.QueueSourceRepository;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

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
    private TicketService ticketService;

    QueueSource stormSource;

    private WebDriver setupDriver() {
        LoggingPreferences logPref = new LoggingPreferences();
        logPref.enable(LogType.BROWSER, Level.OFF);
        logPref.enable(LogType.SERVER, Level.OFF);
        logPref.enable(LogType.DRIVER, Level.OFF);
        logPref.enable(LogType.PROFILER, Level.OFF);
        logPref.enable(LogType.CLIENT, Level.OFF);
        logPref.enable(LogType.PERFORMANCE, Level.OFF);
        DesiredCapabilities capabilities = DesiredCapabilities.htmlUnit();
        capabilities.setCapability(CapabilityType.LOGGING_PREFS, logPref);
        WebDriver driver = new HtmlUnitDriver();
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        return driver;
    }

    private void login(WebDriver driver) {
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

    @Scheduled(fixedDelay = 30000)
    public void processFromQueue() throws Exception {
        // TODO -  Update storm queue count (add column to entity)

        log.debug("Getting next task from queue");
        // Process oldest updated issue from queue
        QueuedForUpdate next = queuedForUpdateService.getNext(TicketSource.STORM);
        log.debug("NEXT: " + (next != null ? next.toString() : null));
        if (next != null) {
            WebDriver driver = setupDriver();
            try {
                processTicket(next, driver);
            } finally {
                driver.quit();
            }
        }
    }

    @Scheduled(fixedDelay = 600000)
    public void buildQueue() throws Exception {
        log.debug("Running storm buildQueue job");
        WebDriver driver = setupDriver();
        login(driver);
        if (stormSource == null) {
            stormSource = queueSourceRepository.findOneByTicketSource(TicketSource.STORM);
            if (stormSource == null) {
                throw new Exception("No Storm queue source found");
            }
        }
        try {
            log.debug("Getting updated tickets");
            List<Ticket> updatedTickets = getUpdatedTickets(driver);
            if (!(updatedTickets == null || updatedTickets.isEmpty())) {
                log.debug("Finding maxdate");
                ZonedDateTime maxDate = getMaxDate(updatedTickets, driver);

                log.debug("Adding updated tickets to queue");
                queuedForUpdateService.addToQueue(updatedTickets);
                log.debug("Setting last added ticket in queueSource");
                if (stormSource.getLastAddedTicket() == null || stormSource.getLastAddedTicket().isBefore(maxDate)) {
                    stormSource.setLastAddedTicket(maxDate);
                    stormSource = queueSourceRepository.save(stormSource);
                }
            }
        } finally {
            driver.quit();
        }
    }

    /**
     * @param updatedTickets
     * @return Max ZonedDateTime, including seconds, for the ticket most recently updated
     */
    private ZonedDateTime getMaxDate(List<Ticket> updatedTickets, WebDriver driver) {
        updatedTickets.sort(new Comparator<Ticket>() {
            @Override
            public int compare(Ticket o1, Ticket o2) {
                return o2.getStormLastUpdated().compareTo(o1.getStormLastUpdated());
            }
        });
        ZonedDateTime maxDate = getUpdatedTimeStamp(updatedTickets.get(0), driver);
        if (updatedTickets.size() == 1) {
            return maxDate;
        }
        for (int x = 1; x < updatedTickets.size(); x++) {
            if (updatedTickets.get(x).getStormLastUpdated().isBefore(updatedTickets.get(0).getStormLastUpdated())) {
                return maxDate;
            }
            ZonedDateTime tempDate = getUpdatedTimeStamp(updatedTickets.get(x), driver);
            if (tempDate.isAfter(maxDate)) {
                maxDate = tempDate;
            }
        }
        return maxDate;
    }

    private ZonedDateTime getUpdatedTimeStamp(Ticket ticket, WebDriver driver) {
        Ticket detailedTicket = getStormTicketInfo(ticket.getStormKey().toString(), driver);
        return detailedTicket.getStormLastUpdated();
    }

    private void processTicket(QueuedForUpdate next, WebDriver driver) {
        Ticket t = getStormTicketInfo(next.getTicketKey(), driver);
        ticketService.mergeFromStorm(t);
    }

    private Ticket getStormTicketInfo(String ticketKey, WebDriver driver) {
        log.debug("Getting storm ticket info for: https://customer.tdchosting.com/tickets/tickets-details/?id=" + ticketKey);
        login(driver);
        driver.navigate().to("https://customer.tdchosting.com/tickets/tickets-details/?id=" + ticketKey);
        Ticket t = new Ticket();
        t.setStormKey(ticketKey);

        /*
        WebElement titleElem = driver.findElement(By.cssSelector(".ticket-details-info > p:nth-child(11)"));
        t.setStormTitle(titleElem.getText().trim());
        */

        WebElement infoContainer = driver.findElements(By.className("ticket-details-info")).get(0);
        WebElement lastUpdatedElem = infoContainer.findElements(By.tagName("tr")).get(3).findElement(By.tagName("td"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        ZoneId zoneId = ZoneId.of("Europe/Paris");
        LocalDateTime dateTime = LocalDateTime.parse(lastUpdatedElem.getText(), formatter);
        t.setStormLastUpdated(ZonedDateTime.of(dateTime, zoneId));

        WebElement statusElem = infoContainer.findElements(By.tagName("tr")).get(4).findElement(By.tagName("td"));
        t.setStormStatus(TicketStatus.parseFromString(statusElem.getText()));
        return t;
    }

    private List<Ticket> getUpdatedTickets(WebDriver driver) throws Exception {
        if (stormSource == null) {
            throw new Exception("No storm source found in database");
        }

        ZonedDateTime cutoffDateTime = stormSource.getLastAddedTicket() == null
            ? ZonedDateTime.now().minusDays(30) : stormSource.getLastAddedTicket();

        return getTicketsUpdatedAfter(cutoffDateTime, driver);
    }

    private List<Ticket> getAllTicketsOnDashboard(WebDriver driver) throws Exception {
        List<Ticket> tickets = new ArrayList<Ticket>();
        List<WebElement> elements = driver.findElements(By.tagName("tr"));
        for (WebElement elem : elements) {
            Ticket ticket = new Ticket();
            List<WebElement> columns = elem.findElements(By.tagName("td"));
            for (WebElement col : columns) {
                switch(col.getAttribute("class")) {
                    case "col1": // TicketKey
                        ticket.setStormKey(col.getText());
                        break;
                    case "type-td": // priority
                        break;
                    case "updated-td": // last updated
                        String timestamp = col.getText();
                        ZonedDateTime dateTime = null;
                        if (timestamp.contains(":")) {
                            if (timestamp.length() == 5) {
                                dateTime = ZonedDateTime.parse(timestamp);
                            } else {
                                LocalDate date = getDateFromDayOfWeek(timestamp);
                                dateTime = ZonedDateTime.parse(date.toString() + "T" + timestamp.substring(4,9) + ":00+01:00");
                            }
                        }
                        //log.debug("No timestamp found: " + col.getText());
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

    private List<Ticket> getTicketsUpdatedAfter(ZonedDateTime cutoffDateTime, WebDriver driver) throws Exception {
        List<Ticket> tickets = getAllTicketsOnDashboard(driver);

        List<Ticket> toBeRemoved = new ArrayList<>();
        for (Ticket t : tickets) {
            if (t.getStormLastUpdated().isBefore(cutoffDateTime)) {
                toBeRemoved.add(t);
            }
        }
        tickets.removeAll(toBeRemoved);
        return tickets;
    }
}
