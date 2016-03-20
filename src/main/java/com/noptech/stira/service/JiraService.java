package com.noptech.stira.service;

import com.noptech.stira.domain.QueueSource;
import com.noptech.stira.domain.QueuedForUpdate;
import com.noptech.stira.domain.Ticket;
import com.noptech.stira.domain.enumeration.TicketSource;
import com.noptech.stira.repository.QueueSourceRepository;
import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

@Service
@Transactional
@PropertySource("classpath:credentials.properties")
public class JiraService {

    private final Logger log = LoggerFactory.getLogger(JiraService.class);

    @Value("${jira.user}")
    private String jiraUser;
    @Value("${jira.password}")
    private String jiraPass;
    @Value("${jira.url}")
    private String jiraUrl;


    @Inject
    private QueueSourceRepository queueSourceRepository;
    @Inject
    private QueuedForUpdateService queuedForUpdateService;
    @Inject
    private TicketService ticketService;

    private QueueSource jiraSource;
    private DateTimeFormatter searchFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm");

    public QueueSource getQueueSource() throws Exception {
        if (jiraSource == null) {
            jiraSource = queueSourceRepository.findOneByTicketSource(TicketSource.JIRA);
            if (jiraSource == null) {
                throw new Exception("Jira queueSource not found");
            }
        }
        return jiraSource;
    }

    private JiraClient getJiraClient() {
        BasicCredentials creds = new BasicCredentials(jiraUser, jiraPass);
        return new JiraClient(jiraUrl, creds);
    }

    @Scheduled(fixedDelay = 100000000)
    public void runJiraJob() throws Exception {
        JiraClient jiraClient = getJiraClient();
        log.debug("Building jira Queue");
        // First check jira for updated issues, add to queue
        ZonedDateTime maxDate = ZonedDateTime.now().minusYears(100);
        ZonedDateTime lastChecked = getQueueSource().getLastAddedTicket() == null ? ZonedDateTime.now().minusDays(30) : jiraSource.getLastAddedTicket();
        Issue.SearchResult sr = jiraClient.searchIssues("updated >= '" + lastChecked.format(searchFormatter) + "' and issuetype in ('Service request', Incident) and project = 'SKAT DIAS'");
        List<Ticket> tickets = new ArrayList<>();
        for (Issue issue : sr.issues) {
            ZonedDateTime updated = ZonedDateTime.parse(issue.getField("updated").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxxx"));
            if (updated.isAfter(maxDate)) {
                maxDate = updated;
            }
            Ticket t = new Ticket(issue);
            tickets.add(t);
        }
        ticketService.mergeFromJira(tickets);
        if (tickets.size() > 0) {
            getQueueSource().setLastAddedTicket(maxDate);
            queueSourceRepository.save(jiraSource);
        }

        // Update Queue count TODO
    }

    @Scheduled(fixedDelay = 400000)
    public void syncJob() throws Exception {
        List<Ticket> tickets = ticketService.findWithMissingFields();
        queuedForUpdateService.addToQueue(tickets);
    }

    @Scheduled(fixedDelay = 30000)
    public void processFromQueue() throws Exception {
        JiraClient jiraClient = getJiraClient();
        // Processing task that was found in storm based on last updated, but not from Jira
        QueuedForUpdate q = queuedForUpdateService.getNext(TicketSource.JIRA);
        if (q != null) {
            Issue.SearchResult sr = jiraClient.searchIssues("project = 'SKAT DIAS' and issueType in (incident, 'Service request') and 'Customer Issue Reference' ~ '#" + q.getTicketKey() + "'");
            log.debug("SEARCH results: " + sr.toString());
            log.debug("SEARCH results: " + sr.issues.toString());
            if (sr.issues == null || sr.issues.size() != 1) {
                log.debug("Could not find specific issue matching: " + q.getTicketKey());
                ticketService.flagTicketByStormKey(q.getTicketKey());
                return;
            }
            Ticket ticket = new Ticket(sr.issues.get(0));
            ticket.setStormKey(q.getTicketKey());
            ticketService.mergeFromStorm(ticket);
        }
    }

}
