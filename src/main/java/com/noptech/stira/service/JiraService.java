package com.noptech.stira.service;

import com.noptech.stira.domain.QueueSource;
import com.noptech.stira.domain.Ticket;
import com.noptech.stira.domain.enumeration.TicketSource;
import com.noptech.stira.repository.QueueSourceRepository;
import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
    private JiraClient jiraClient;
    private DateTimeFormatter searchFormatter;

    public void setup() throws Exception {
        searchFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm");
        if (jiraSource == null) {
            jiraSource = queueSourceRepository.findOneByTicketSource(TicketSource.JIRA);
            if (jiraSource == null) {
                throw new Exception("Jira queueSource not found");
            }
        }
        if (jiraClient == null) {
            BasicCredentials creds = new BasicCredentials(jiraUser, jiraPass);
            jiraClient = new JiraClient(jiraUrl, creds);
        }
    }

    @Scheduled(fixedDelay = 10000)
    public void runJiraJob() throws Exception {
        setup();
        log.debug("Building jira Queue");
        // First check jira for updated issues, add to queue
        LocalDateTime maxDate = LocalDateTime.MIN;
        LocalDateTime lastChecked = jiraSource.getLastAddedTicket() == null ? LocalDateTime.now().minusDays(30) : jiraSource.getLastAddedTicket();
        Issue.SearchResult sr = jiraClient.searchIssues("updated >= '" + lastChecked.format(searchFormatter) + "' and issuetype = Incident and project = 'SKAT DIAS'");
        List<Ticket> tickets = new ArrayList<>();
        for (Issue issue : sr.issues) {
            LocalDateTime updated = LocalDateTime.parse(issue.getField("updated").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxxx"));
            if (updated.isAfter(maxDate)) {
                maxDate = updated;
            }
            Ticket t = new Ticket(issue);
            tickets.add(t);
        }
        ticketService.mergeFromJira(tickets);
        if (tickets.size() > 0) {
            jiraSource.setLastAddedTicket(maxDate);
            queueSourceRepository.save(jiraSource);
        }

        // Update Queue count TODO
    }
}
